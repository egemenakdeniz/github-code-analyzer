package org.example.githubfiles.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.model.Session;
import org.example.githubfiles.model.Result;

@Service
public class PdfReportService {

    public String generateReportPdf(Repository repo, Session session, List<Result> results) throws IOException {
        if (results.stream().noneMatch(r -> r.getSuggestions() != null && !r.getSuggestions().isBlank())) {
            return null;
        }
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "/Desktop/reports";

        String fileName = String.format("%s/%d_%s_%s_%s_%s_%s.pdf",
                desktopPath,
                repo.getId(),
                repo.getUserName(),
                repo.getRepoName(),
                repo.getBranchName(),
                session.getModel_name(),
                LocalDateTime.now().toString().replace(":", "-"));

        File file = new File(fileName);
        file.getParentFile().mkdirs();

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);

            PDFont font = PDType1Font.HELVETICA;
            float y = 700;

            for (Result r : results) {
                if (y < 100) {
                    stream.close();
                    page = new PDPage();
                    doc.addPage(page);
                    stream = new PDPageContentStream(doc, page);
                    y = 700;
                }
                stream.beginText();
                stream.setFont(font, 10);
                stream.newLineAtOffset(50, y);
                stream.showText("FILE: " + r.getFile().getPath());
                stream.newLineAtOffset(0, -15);
                stream.showText("CLASS: " + r.getClass_name());
                stream.newLineAtOffset(0, -15);
                stream.showText("SEVERITY: " + r.getSeverity());
                stream.newLineAtOffset(0, -15);
                stream.showText("ISSUE: " + r.getIssue());
                stream.newLineAtOffset(0, -15);
                stream.showText("SUGGESTION: " + r.getSuggestions());
                stream.endText();
                y -= 90;
            }
            stream.close();
            doc.save(file);
        }
        return fileName.replace("\\", "/");
    }
}
