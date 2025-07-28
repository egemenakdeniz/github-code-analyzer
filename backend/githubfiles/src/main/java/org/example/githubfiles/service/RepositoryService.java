package org.example.githubfiles.service;

import lombok.AllArgsConstructor;
import org.example.githubfiles.exception.badrequest.InvalidRepositoryMetadataException;
import org.example.githubfiles.exception.conflict.RepositoryAlreadyExistException;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.RepositoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final GithubService githubService;
    private final FileService fileService;

    public void saveRepositoryAndFiles(Repository repository) {

        if (repository == null) {
            throw new InvalidRepositoryMetadataException("Repository cannot be null");
        }

        boolean exists = repositoryRepository.findByUserNameAndRepoNameAndBranchName(repository.getUserName(), repository.getRepoName(), repository.getBranchName()).isPresent();
        if (exists) {
            throw new RepositoryAlreadyExistException("Repository " + repository.getRepoName() + " already exists");
        }

        repository.setUrl("https://github.com/" + repository.getUserName() + "/" + repository.getRepoName());
        repository.setCreatedAt(LocalDate.now());
        repository.setUpToDate(true);

        List<File> fetchedFiles = githubService.fetchFilesFromRepo(repository);

        Repository savedRepo = repositoryRepository.save(repository);
        fileService.saveFilesWithRepository(fetchedFiles, savedRepo);
    }
}
