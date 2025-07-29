package org.example.githubfiles;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import org.example.githubfiles.exception.badgateway.PdfEmptyResultException;
import org.example.githubfiles.exception.internal.PdfDocumentCreationException;
import org.example.githubfiles.exception.internal.PdfFontLoadException;
import org.example.githubfiles.model.File;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.model.Result;
import org.example.githubfiles.service.PdfReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfReportServiceTest {

    private PdfReportService pdfReportService;

    @BeforeEach
    void setUp() {
        pdfReportService = new PdfReportService(); // gerçek nesne
    }

    @Test
    void shouldThrowPdfEmptyResultExceptionWhenAllSuggestionsAreEmpty() {
        Repository repo = new Repository();
        repo.setUserName("test-user");
        repo.setRepoName("test-repo");

        Result emptyResult = new Result();
        emptyResult.setSuggestions(""); // empty suggestion

        assertThrows(PdfEmptyResultException.class, () -> {
            pdfReportService.generateReportPdfBytes(repo, List.of(emptyResult));
        });
    }

    @Test
    void shouldGeneratePdfSuccessfullyWhenSuggestionsExist() throws Exception {
        Repository repo = new Repository();
        repo.setUserName("egemen");
        repo.setRepoName("backend");

        File file = new File();
        file.setPath("src/main/java/Test.java");

        Result result = new Result();
        result.setFile(file);
        result.setClass_name("TestClass");
        result.setSeverity("HIGH");
        result.setIssue("Potential memory leak");
        result.setSuggestions("Use try-with-resources");

        byte[] pdfBytes = pdfReportService.generateReportPdfBytes(repo, List.of(result));
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void shouldThrowPdfFontLoadExceptionWhenFontFactoryFails() {
        try (MockedStatic<FontFactory> mocked = mockStatic(FontFactory.class)) {
            mocked.when(() -> FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20))
                    .thenThrow(new RuntimeException("font error"));

            mocked.when(() -> FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16))
                    .thenReturn(new Font());
            mocked.when(() -> FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))
                    .thenReturn(new Font());
            mocked.when(() -> FontFactory.getFont(FontFactory.HELVETICA, 11))
                    .thenReturn(new Font());

            Repository repo = new Repository();
            repo.setUserName("egemen");
            repo.setRepoName("backend");

            Result result = new Result();
            result.setSuggestions("dummy suggestion");

            assertThrows(PdfFontLoadException.class, () -> {
                pdfReportService.generateReportPdfBytes(repo, List.of(result));
            });
        }
    }

    @Test
    void shouldThrowPdfDocumentCreationExceptionWhenTitleAdditionFails() {
        try (MockedStatic<FontFactory> mocked = mockStatic(FontFactory.class)) {
            mocked.when(() -> FontFactory.getFont(anyString(), anyInt()))
                    .thenReturn(new Font());

            Repository repo = new Repository();
            repo.setUserName(null); // this will cause NPE in document.add(title)
            repo.setRepoName("backend");

            Result result = new Result();
            result.setSuggestions("something");

            assertThrows(PdfDocumentCreationException.class, () -> {
                pdfReportService.generateReportPdfBytes(repo, List.of(result));
            });
        }
    }

    @Test
    void shouldSkipResultWhenSuggestionIsNull() throws Exception {
        Repository repo = new Repository();
        repo.setUserName("egemen");
        repo.setRepoName("backend");

        File file = new File();
        file.setPath("src/main/java/Test.java");

        Result nullSuggestion = new Result();
        nullSuggestion.setFile(file);
        nullSuggestion.setSuggestions(null); // skipped

        Result valid = new Result();
        valid.setFile(file);
        valid.setClass_name("Cls");
        valid.setSeverity("LOW");
        valid.setIssue("meh");
        valid.setSuggestions("fix it");

        byte[] pdfBytes = pdfReportService.generateReportPdfBytes(repo, List.of(nullSuggestion, valid));
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}

