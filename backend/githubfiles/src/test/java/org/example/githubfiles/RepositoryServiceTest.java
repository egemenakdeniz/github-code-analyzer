package org.example.githubfiles;

import org.example.githubfiles.exception.badrequest.InvalidRepositoryMetadataException;
import org.example.githubfiles.exception.conflict.RepositoryAlreadyExistException;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.RepositoryRepository;
import org.example.githubfiles.service.FileService;
import org.example.githubfiles.service.GithubService;
import org.example.githubfiles.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Mock
    private RepositoryRepository repositoryRepository;

    @Mock
    private GithubService githubService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RepositoryService repositoryService;

    private Repository repo;
    private List<File> files;

    @BeforeEach
    void setUp() {
        repo = new Repository();
        repo.setUserName("egemen");
        repo.setRepoName("project");
        repo.setBranchName("main");

        File file = new File();
        file.setPath("Test.java");
        files = List.of(file);
    }

    @Test
    void shouldThrowWhenRepositoryIsNull() {
        assertThrows(InvalidRepositoryMetadataException.class, () -> {
            repositoryService.saveRepositoryAndFiles(null);
        });
    }

    @Test
    void shouldThrowWhenRepositoryAlreadyExists() {
        when(repositoryRepository.findByUserNameAndRepoNameAndBranchName("egemen", "project", "main"))
                .thenReturn(Optional.of(new Repository()));

        assertThrows(RepositoryAlreadyExistException.class, () -> {
            repositoryService.saveRepositoryAndFiles(repo);
        });

        verify(repositoryRepository, never()).save(any());
        verify(githubService, never()).fetchFilesFromRepo(any());
    }

    @Test
    void shouldSaveNewRepositoryAndFiles() {
        when(repositoryRepository.findByUserNameAndRepoNameAndBranchName("egemen", "project", "main"))
                .thenReturn(Optional.empty());

        when(githubService.fetchFilesFromRepo(repo)).thenReturn(files);
        when(repositoryRepository.save(repo)).thenReturn(repo);

        repositoryService.saveRepositoryAndFiles(repo);

        assertEquals("https://github.com/egemen/project", repo.getUrl());
        assertEquals(LocalDate.now(), repo.getCreatedAt());
        assertTrue(repo.getUpToDate());

        verify(repositoryRepository).save(repo);
        verify(fileService).saveFilesWithRepository(files, repo);
    }
}