package org.example.githubfiles.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.githubfiles.exception.badrequest.EmptyFileListException;
import org.example.githubfiles.exception.badrequest.InvalidRepositoryMetadataException;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.example.githubfiles.repository.RepositoryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final GithubService githubService;
    private final RepositoryRepository repositoryRepository;

    public void saveFilesWithRepository(List<File> files, Repository repository) {
        if (files == null || files.isEmpty()) throw new EmptyFileListException("Cannot save empty or null file list.");
        if (repository == null) throw new InvalidRepositoryMetadataException("Repository cannot be null when saving files.");;
        for (File file : files) {
            file.setRepository(repository);
            fileRepository.save(file);
        }
    }

    @Transactional
    public void checkAndMarkOutdatedRepositories() {
        List<Repository> allRepos = repositoryRepository.findAll();

        for (Repository repo : allRepos) {
            if (Boolean.FALSE.equals(repo.getUpToDate())) continue;

            List<File> currentFiles = fileRepository.findByRepositoryIdAndIsActiveTrue(repo.getId());
            List<File> latestFiles = githubService.fetchFilesFromRepo(
                    repo
            );
            if (hasChanged(currentFiles, latestFiles)) {
                repositoryRepository.markAsOutdated(repo.getId());
            }
        }
    }

    private boolean hasChanged(List<File> currentFiles, List<File> latestFiles) {
        if (currentFiles == null || currentFiles.isEmpty()) throw new EmptyFileListException("Current file list is empty or null. Cannot perform comparison.");
        if (latestFiles == null || latestFiles.isEmpty()) throw new EmptyFileListException("Latest file list is empty or null. Cannot perform comparison.");

        Map<String, String> currentMap = currentFiles.stream().collect(Collectors.toMap(File::getPath, File::getHash));
        Map<String, String> latestMap = latestFiles.stream().collect(Collectors.toMap(File::getPath, File::getHash));

        if (currentMap.size() != latestMap.size()) return true;

        for (Map.Entry<String, String> entry : latestMap.entrySet()) {
            log.debug("Current hash: {}", currentMap.get(entry.getKey()));
            log.debug("Latest hash: {}" , latestMap.get(entry.getKey()));
            if (!currentMap.containsKey(entry.getKey()) || !entry.getValue().equals(currentMap.get(entry.getKey()))) {
                return true;
            }
        }
        return false;
    }
}
