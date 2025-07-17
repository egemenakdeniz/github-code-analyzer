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

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;


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



    public String generateReportPdf2(Repository repo, Session session, List<Result> results) throws Exception {
        if (results.stream().noneMatch(r -> r.getSuggestions() != null && !r.getSuggestions().isBlank())) {
            return null;
        }

        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "/Desktop/reports";
        new File(desktopPath).mkdirs();

        String fileName = String.format("%s/%d_%s_%s_%s_%s_%s.pdf",
                desktopPath,
                repo.getId(),
                repo.getUserName(),
                repo.getRepoName(),
                repo.getBranchName(),
                session.getModel_name(),
                LocalDateTime.now().toString().replace(":", "-"));

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font titleFont_ai = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Paragraph title = new Paragraph(repo.getUserName()+"-"+repo.getRepoName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        Paragraph title_ai = new Paragraph("AI Code Analysis Report", titleFont_ai);
        title_ai.setAlignment(Element.ALIGN_CENTER);
        title_ai.setSpacingAfter(20);
        document.add(title_ai);

        for (Result r : results) {
            if (r.getSuggestions() == null || r.getSuggestions().isBlank()) continue;

            Paragraph p = new Paragraph();
            p.setSpacingAfter(10);

            p.add(new Chunk("FILE: ", boldFont));
            p.add(new Chunk(r.getFile().getPath() + "\n", normalFont));

            p.add(new Chunk("CLASS: ", boldFont));
            p.add(new Chunk(String.valueOf(r.getClass_name()) + "\n", normalFont));

            p.add(new Chunk("SEVERITY: ", boldFont));
            p.add(new Chunk(r.getSeverity() + "\n", normalFont));

            p.add(new Chunk("ISSUE: ", boldFont));
            p.add(new Chunk(r.getIssue() + "\n", normalFont));

            p.add(new Chunk("SUGGESTION: ", boldFont));
            p.add(new Chunk(r.getSuggestions() + "\n", normalFont));

            document.add(p);
        }

        document.close();
        return fileName.replace("\\", "/");
    }
}
