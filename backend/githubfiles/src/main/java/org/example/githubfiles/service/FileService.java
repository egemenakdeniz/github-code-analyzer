package org.example.githubfiles.service;

import jakarta.transaction.Transactional;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.example.githubfiles.repository.RepositoryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final GithubService githubService;
    private final RepositoryRepository repositoryRepository;


    public FileService(FileRepository fileRepository,GithubService githubService,RepositoryRepository repositoryRepository) {
        this.fileRepository = fileRepository;
        this.githubService = githubService;
        this.repositoryRepository = repositoryRepository;
    }

    public void saveFilesWithRepository(List<File> files, Repository repository) {
        for (File file : files) {
            file.setRepository(repository);
            fileRepository.save(file);
        }
    }

    @Transactional
    public void checkAndMarkOutdatedRepositories() {
        List<Repository> allRepos = repositoryRepository.findAll();

        for (Repository repo : allRepos) {
            if (!repo.getUpToDate()) continue;

            List<File> currentFiles = fileRepository.findByRepositoryIdAndIsActiveTrue(repo.getId());
            List<File> latestFiles = githubService.fetchFilesFromRepo(
                    repo.getUserName(), repo.getRepoName(), repo.getBranchName()
            );
            if (hasChanged(currentFiles, latestFiles)) {
                repositoryRepository.markAsOutdated(repo.getId());
            }
        }
    }

    private boolean hasChanged(List<File> currentFiles, List<File> latestFiles) {
        Map<String, String> currentMap = currentFiles.stream().collect(Collectors.toMap(File::getPath, File::getHash));
        Map<String, String> latestMap = latestFiles.stream().collect(Collectors.toMap(File::getPath, File::getHash));

        if (currentMap.size() != latestMap.size()) return true;

        for (Map.Entry<String, String> entry : latestMap.entrySet()) {
            System.out.println("Current hash: " + currentMap.get(entry.getKey()));
            System.out.println("Latest hash: " + latestMap.get(entry.getKey()));
            if (!currentMap.containsKey(entry.getKey()) || !entry.getValue().equals(currentMap.get(entry.getKey()))) {
                return true;
            }
        }
        return false;
    }
}
