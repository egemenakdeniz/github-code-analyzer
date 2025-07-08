package org.example.githubfiles.controller;
import jakarta.persistence.EntityManager;
import org.example.githubfiles.repository.RepositoryRepository;
import org.example.githubfiles.repository.SessionRepository;
import org.example.githubfiles.service.OllamaService;
import org.example.githubfiles.model.*;
import org.example.githubfiles.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.service.FileService;
import org.example.githubfiles.service.GithubService;
import org.springframework.web.server.ResponseStatusException;
import org.example.githubfiles.repository.FileRepository;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {


    private final RepositoryService repositoryService;

    @Autowired
    private RepositoryRepository repositoryRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private GithubService githubService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private EntityManager entityManager;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping("/import")
    public ResponseEntity<String> analyzeRepository(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch) {

        try {
            repositoryService.saveRepositoryAndFiles(owner, repo, branch);
            return ResponseEntity.ok("Repository and files saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/loaded")
    public List<Map<String, Object>> getLoadedRepositories() {
        List<Repository> repos = repositoryRepository.findAll();

        return repos.stream().map(repo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", repo.getId());
            map.put("username", repo.getUserName());
            map.put("repo", repo.getRepoName());
            map.put("branch", repo.getBranchName());

            boolean hasAnalysis = sessionRepository.existsByRepository(repo);
            map.put("hasAnalysis", hasAnalysis);

            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/has-any-changed")
    public List<Map<String, Object>> checkAllRepositoriesForChanges() {
        fileService.checkAndMarkOutdatedRepositories();

        entityManager.clear();

        List<Repository> allRepos = repositoryRepository.findAll();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Repository repo : allRepos) {
            Map<String, Object> result = new HashMap<>();
            result.put("owner", repo.getUserName());
            result.put("repo", repo.getRepoName());
            result.put("branch", repo.getBranchName());
            result.put("changed", !repo.getUpToDate());// true = değişmiş, false = güncel
            //System.out.println(repo.getUpToDate());
            results.add(result);
        }
        return results;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateRepository(@RequestBody Map<String, String> repoInfo) {
        String owner = repoInfo.get("owner");
        String repo = repoInfo.get("repo");
        String branch = repoInfo.get("branch");

        Optional<Repository> repoOpt = repositoryRepository.findByUserNameAndRepoNameAndBranchName(owner, repo, branch);
        if (repoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repository not found.");
        }

        Repository repository = repoOpt.get();

        fileRepository.deactivateAllByRepositoryId(repository.getId());

        List<File> newFiles = githubService.fetchFilesFromRepo(owner, repo, branch);

        for (File file : newFiles) {
            file.setRepository(repository);
            file.setActive(true);
            fileRepository.save(file);
        }

        repository.setUpToDate(true);
        repositoryRepository.save(repository);

        return ResponseEntity.ok("Repository updated successfully.");
    }
}
