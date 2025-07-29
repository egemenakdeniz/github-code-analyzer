package org.example.githubfiles.service;

import org.example.githubfiles.exception.badgateway.*;
import org.example.githubfiles.exception.badrequest.InvalidRepositoryMetadataException;
import org.example.githubfiles.exception.internal.GithubTokenMissingException;
import org.example.githubfiles.exception.notfound.FileNotFoundOnGithubException;
import org.example.githubfiles.exception.notfound.RepositoryNotFoundException;
import org.example.githubfiles.exception.toomanyrequests.GithubApiRateLimitExceededException;
import org.example.githubfiles.exception.unauthorized.GithubAuthenticationException;
import org.example.githubfiles.exception.unavailable.NetworkUnavailableException;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.utils.NetworkUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.json.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GithubService {

    //private final String token = System.getenv("GITHUB_TOKEN");
    private final String GITHUB_API = "https://api.github.com";

    private final String token;
    public GithubService() {
        this.token = System.getenv("GITHUB_TOKEN");
    }
    // Test için kullanılabilecek constructor
    public GithubService(String token) {
        this.token = token;
    }


    private static final Set<String> CODE_EXTENSIONS = Set.of(
            ".java", ".py", ".js", ".ts", ".jsx", ".tsx",
            ".c", ".cpp", ".cs", ".rb", ".go", ".php",
            ".swift", ".kt", ".rs", ".html", ".css", ".scss",
            ".xml", ".yml", ".yaml", ".sh", ".bat",".ino"
    );

    public List<File> fetchFilesFromRepo(Repository repository) {

        if (!NetworkUtils.isInternetAvailable()) {
            throw new NetworkUnavailableException("The server is not connected to the internet. Please check your network connection.");
        }
        if (token == null || token.isBlank()) {
            throw new GithubTokenMissingException("GitHub token is not set in environment variables");
        }

        if (repository.getUserName() == null || repository.getRepoName() == null || repository.getBranchName() == null) {
            throw new InvalidRepositoryMetadataException("Repository metadata (username, reponame or branch) is incomplete");
        }
        List<File> files = new ArrayList<>();
        String treeUrl = String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", GITHUB_API, repository.getUserName(), repository.getRepoName(), repository.getBranchName());

        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = new RestTemplate().exchange(treeUrl, HttpMethod.GET, entity, String.class);
        }catch (HttpClientErrorException.Unauthorized e) {
            throw new GithubAuthenticationException("GitHub token invalid or unauthorized for repository: " + repository.getRepoName());
        }
        catch (HttpClientErrorException.NotFound e) {
            throw new RepositoryNotFoundException("Repository or branch not found: " + repository.getRepoName());
        }
        catch (HttpClientErrorException.Forbidden e){
            throw new GithubApiRateLimitExceededException("Github API rate limit exceeded");
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONArray tree;
            try {
                tree = new JSONObject(response.getBody()).getJSONArray("tree");
            } catch (JSONException e) {
                throw new MalformedGithubApiResponseException("Failed to parse GitHub API response");
            }

            for (int i = 0; i < tree.length(); i++) {
                JSONObject item = tree.getJSONObject(i);
                if ("blob".equals(item.getString("type"))) {
                    String path = item.getString("path");
                    String sha = item.getString("sha");

                    if (!isCodeFile(path)) {continue;}
                    try {
                        String content = fetchFileContent(repository.getUserName(), repository.getRepoName(),path, repository.getBranchName());

                        if (content.contains("\u0000")) {
                            //System.out.println("Skipped binary file with null byte: " + path);
                            continue;
                        }

                        File file = new File();
                        file.setPath(path);
                        file.setContent(content);
                        file.setType("code"); // Şimdilik sabit
                        file.setHash(sha);
                        file.setRepository(null);

                        files.add(file);
                    } catch (Exception e) {
                        throw new FileContentFetchException("Failed to parse GitHub API response");
                    }
                }
            }
        }
        return files;
    }


    public String fetchFileContent(String owner, String repo, String path, String branch) {
        String fileUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", GITHUB_API, owner, repo, path, branch);
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = new RestTemplate().exchange(fileUrl, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new FileNotFoundOnGithubException("File not found on GitHub: " + path);
        }
         catch (HttpClientErrorException.Forbidden e) {
            if (e.getResponseBodyAsString().contains("rate limit")) {
                throw new GithubApiRateLimitExceededException("Rate limit exceeded while fetching file: " + path);
            } else {
                throw new GithubAuthenticationException("GitHub token invalid or unauthorized for file: " + path);
            }
        } catch (RestClientException e) {
            throw new GithubServiceException("Unexpected error while fetching file from GitHub: " + path);
        }

        try {
            JSONObject json = new JSONObject(response.getBody());
            if (!json.has("content")) {
                throw new FileContentMissingException("File content not found in GitHub response for file: " + path);
            }

            String encoded = json.getString("content");
            return new String(Base64.getDecoder().decode(encoded.replaceAll("\\s", "")), StandardCharsets.UTF_8);

        } catch (JSONException | IllegalArgumentException e) {
            throw new FileContentParseException("Failed to parse or decode content for file: " + path);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    private boolean isCodeFile(String path) {
        return CODE_EXTENSIONS.stream().anyMatch(path::endsWith);
    }
}
