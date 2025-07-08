package org.example.githubfiles.service;

import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.RepositoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final GithubService githubService;
    private final FileService fileService;

    public RepositoryService(RepositoryRepository repositoryRepository, GithubService githubService, FileService fileService) {
        this.repositoryRepository = repositoryRepository;
        this.githubService = githubService;
        this.fileService = fileService;
    }

    public void saveRepositoryAndFiles(String username, String repoName, String branch) {
        boolean exists = repositoryRepository.findByUserNameAndRepoNameAndBranchName(username, repoName, branch).isPresent();

        if (exists) {
            throw new IllegalArgumentException("Repository " + repoName + " already exists");
        }

        Repository repo = new Repository();
        repo.setUserName(username);
        repo.setRepoName(repoName);
        repo.setBranchName(branch);
        repo.setUrl("https://github.com/" + username + "/" + repoName);
        repo.setCreatedAt(LocalDate.now());
        repo.setUpToDate(true);

        Repository savedRepo = repositoryRepository.save(repo);

        List<File> fetchedFiles = githubService.fetchFilesFromRepo(username, repoName, branch);
        fileService.saveFilesWithRepository(fetchedFiles, savedRepo);
    }
}
