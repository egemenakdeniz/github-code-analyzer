package org.example.githubfiles;

import org.example.githubfiles.model.*;
import org.example.githubfiles.repository.*;
import org.example.githubfiles.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

public class AnalysisServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private ResultRepository resultRepository;
    @Mock private SessionRepository sessionRepository;
    @Mock private RepositoryRepository repositoryRepository;
    @Mock private FileRepository fileRepository;
    @Mock private PdfReportService pdfReportService;
    @Mock private AiService aiService;

    @InjectMocks private AnalysisService analysisService;

    private Repository repo;
    private File file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repo = new Repository();
        repo.setId(1L);
        repo.setUserName("egemen");
        repo.setRepoName("test-repo");
        repo.setBranchName("main");

        file = new File();
        file.setId(1L);
        file.setPath("test/File.java");
        file.setContent("public class File {}");
        file.setActive(true);
    }

    @Test
    void shouldAnalyzeSuccessfully() throws Exception{
        // mock repository
        when(repositoryRepository.findByUserNameAndRepoNameAndBranchName("egemen", "test-repo", "main"))
                .thenReturn(Optional.of(repo));
        when(fileRepository.findByRepositoryIdAndIsActiveTrue(1L))
                .thenReturn(List.of(file));

        // mock AI response
        String aiResponse = "FILE: test/File.java\n" +
                "CLASS: MyClass\n" +
                "SEVERITY: MID\n" +
                "ISSUE: Unused import\n" +
                "SUGGESTION: Remove the unused import";

        when(aiService.ask(eq("ollama"), anyString(), anyString()))
                .thenReturn(aiResponse);
        when(fileRepository.findByPathAndRepository_IdAndIsActiveTrue("test/File.java", 1L))
                .thenReturn(Optional.of(file));
        when(pdfReportService.generateReportPdfBytes(any(), any()))
                .thenReturn("pdf".getBytes());

        // çalıştır
        analysisService.analyzeRepository(repo, "ollama", "gemma");

        // verify
        verify(resultRepository, times(1)).saveAll(anyList());
        verify(reportRepository, times(1)).insertReport(any(), any(), any());
    }

    @Test
    void shouldAnalyzeWithOpenAI() throws Exception {
        when(repositoryRepository.findByUserNameAndRepoNameAndBranchName(any(), any(), any()))
                .thenReturn(Optional.of(repo));
        when(fileRepository.findByRepositoryIdAndIsActiveTrue(anyLong()))
                .thenReturn(List.of(file));
        when(aiService.ask(eq("openai"), any(), any()))
                .thenReturn("FILE: test/File.java\n" +
                        "CLASS: MyClass\n" +
                        "SEVERITY: MID\n" +
                        "ISSUE: X\n" +
                        "SUGGESTION: Y");
        when(fileRepository.findByPathAndRepository_IdAndIsActiveTrue(anyString(), anyLong()))
                .thenReturn(Optional.of(file));
        when(pdfReportService.generateReportPdfBytes(any(), any()))
                .thenReturn("pdf".getBytes());

        analysisService.analyzeRepository(repo, "openai", "gpt-4");
        verify(resultRepository).saveAll(any());
        verify(reportRepository).insertReport(any(), any(), any());
    }

    @Test
    void shouldThrowWhenRepoNotFound() {
        when(repositoryRepository.findByUserNameAndRepoNameAndBranchName(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThatCode(() -> analysisService.analyzeRepository(repo, "ollama", "gemma"))
                .isInstanceOf(org.example.githubfiles.exception.notfound.RepositoryNotFoundException.class);
    }
}