package org.example.githubfiles.service;

import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GithubService {

    private final String token = System.getenv("GITHUB_TOKEN");
    private final String GITHUB_API = "https://api.github.com";

    private static final Set<String> CODE_EXTENSIONS = Set.of(
            ".java", ".py", ".js", ".ts", ".jsx", ".tsx",
            ".c", ".cpp", ".cs", ".rb", ".go", ".php",
            ".swift", ".kt", ".rs", ".html", ".css", ".scss",
            ".xml", ".yml", ".yaml", ".sh", ".bat",".ino"
    );

    public List<File> fetchFilesFromRepo(String owner, String repo, String branch) {
        List<File> files = new ArrayList<>();
        //System.out.println("GITHUB_TOKEN = " + token);


        String treeUrl = String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", GITHUB_API, owner, repo, branch);

        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = new RestTemplate().exchange(treeUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONArray tree = new JSONObject(response.getBody()).getJSONArray("tree");

            for (int i = 0; i < tree.length(); i++) {
                JSONObject item = tree.getJSONObject(i);
                if ("blob".equals(item.getString("type"))) {
                    String path = item.getString("path");
                    String sha = item.getString("sha");

                    if (isCodeFile(path)) {continue;}
                    //System.out.println(sha);
                    try {
                        String content = fetchFileContent(owner, repo, path, branch);
                        content = content.replace("\u0000", "");

                        File file = new File();
                        file.setPath(path);
                        file.setContent(content);
                        file.setType("code"); // Şimdilik sabit
                        file.setHash(sha);
                        file.setRepository(null);

                        files.add(file);
                    } catch (Exception ignored) {}
                }
            }
        }
        return files;
    }

    public List<File> fetchFilesFromRepo(Repository repository) {
        List<File> files = new ArrayList<>();
        //System.out.println("GITHUB_TOKEN = " + token);
        String treeUrl = String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", GITHUB_API, repository.getUserName(), repository.getRepoName(), repository.getBranchName());

        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = new RestTemplate().exchange(treeUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONArray tree = new JSONObject(response.getBody()).getJSONArray("tree");

            for (int i = 0; i < tree.length(); i++) {
                JSONObject item = tree.getJSONObject(i);
                if ("blob".equals(item.getString("type"))) {
                    String path = item.getString("path");
                    String sha = item.getString("sha");


                    if (!isCodeFile(path)) {continue;}
                    //System.out.println(sha);
                    try {
                        String content = fetchFileContent(repository.getUserName(), repository.getRepoName(),path, repository.getBranchName());

                        if (content.contains("\u0000")) {
                            System.out.println("Skipped binary file with null byte: " + path);
                            continue;
                        }

                        File file = new File();
                        file.setPath(path);
                        file.setContent(content);
                        file.setType("code"); // Şimdilik sabit
                        file.setHash(sha);
                        file.setRepository(null);

                        files.add(file);
                    } catch (Exception ignored) {}
                }
            }
        }
        return files;
    }


    private String fetchFileContent(String owner, String repo, String path, String branch) {
        String fileUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", GITHUB_API, owner, repo, path, branch);
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = new RestTemplate().exchange(fileUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject json = new JSONObject(response.getBody());
            String encoded = json.getString("content");
            return new String(Base64.getDecoder().decode(encoded.replaceAll("\\s", "")), StandardCharsets.UTF_8);
        }
        return "";
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
