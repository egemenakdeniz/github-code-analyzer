package org.example.githubfiles;

import org.example.githubfiles.exception.badrequest.EmptyFileListException;
import org.example.githubfiles.exception.badrequest.InvalidRepositoryMetadataException;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.FileRepository;
import org.example.githubfiles.repository.RepositoryRepository;
import org.example.githubfiles.service.FileService;
import org.example.githubfiles.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    @Mock private FileRepository fileRepository;
    @Mock private RepositoryRepository repositoryRepository;
    @Mock private GithubService githubService;

    @InjectMocks private FileService fileService;

    private Repository repository;
    private File file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        repository = new Repository();
        repository.setId(1L);
        repository.setUpToDate(true);

        file = new File();
        file.setId(1L);
        file.setPath("test/File.java");
        file.setHash("abc123");
        file.setActive(true);
    }

    @Test
    void shouldSaveFilesWithRepository() {
        fileService.saveFilesWithRepository(List.of(file), repository);

        verify(fileRepository, times(1)).save(file);
        assertThat(file.getRepository()).isEqualTo(repository);
    }

    @Test
    void shouldThrowWhenFilesNull() {
        assertThatThrownBy(() -> fileService.saveFilesWithRepository(null, repository))
                .isInstanceOf(EmptyFileListException.class);
    }

    @Test
    void shouldThrowWhenRepositoryNull() {
        assertThatThrownBy(() -> fileService.saveFilesWithRepository(List.of(file), null))
                .isInstanceOf(InvalidRepositoryMetadataException.class);
    }

    @Test
    void shouldMarkRepositoryAsOutdatedIfChanged() {
        File updated = new File();
        updated.setPath("test/File.java");
        updated.setHash("different_hash");

        when(repositoryRepository.findAll()).thenReturn(List.of(repository));
        when(fileRepository.findByRepositoryIdAndIsActiveTrue(1L)).thenReturn(List.of(file));
        when(githubService.fetchFilesFromRepo(repository)).thenReturn(List.of(updated));

        fileService.checkAndMarkOutdatedRepositories();

        verify(repositoryRepository).markAsOutdated(1L);
    }

    @Test
    void shouldNotMarkIfSameHashes() {
        when(repositoryRepository.findAll()).thenReturn(List.of(repository));
        when(fileRepository.findByRepositoryIdAndIsActiveTrue(1L)).thenReturn(List.of(file));
        when(githubService.fetchFilesFromRepo(repository)).thenReturn(List.of(file));

        fileService.checkAndMarkOutdatedRepositories();

        verify(repositoryRepository, never()).markAsOutdated(any());
    }
}