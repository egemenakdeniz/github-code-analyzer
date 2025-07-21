package org.example.githubfiles.service;

import org.example.githubfiles.exception.PdfDocumentCreationException;
import org.example.githubfiles.exception.PdfEmptyResultException;
import org.example.githubfiles.exception.PdfFontLoadException;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.model.Result;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;


@Service
public class PdfReportService {

    public byte[] generateReportPdfBytes(Repository repo, List<Result> results) throws Exception {
        if (results.stream().noneMatch(r -> r.getSuggestions() != null && !r.getSuggestions().isBlank())) {
            throw new PdfEmptyResultException("No suggestions found in the results. Skipping PDF generation.");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont, titleFont_ai, boldFont, normalFont;
        try {
            titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            titleFont_ai = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        } catch (Exception e) {
            throw new PdfFontLoadException("Failed to load fonts for PDF");
        }

        try {
            Paragraph title = new Paragraph(repo.getUserName() + "-" + repo.getRepoName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Paragraph date = new Paragraph(LocalDateTime.now().toLocalDate().toString());
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(30);
            document.add(date);

            Paragraph title_ai = new Paragraph("AI Code Analysis Report", titleFont_ai);
            title_ai.setAlignment(Element.ALIGN_CENTER);
            title_ai.setSpacingAfter(20);
            document.add(title_ai);
        } catch (Exception e) {
            throw new PdfDocumentCreationException("Failed to create document structure");
        }


        for (Result r : results) {
            if (r.getSuggestions() == null || r.getSuggestions().isBlank()) continue;
            try {
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
            } catch (Exception e) {
                throw new PdfDocumentCreationException("Error while adding paragraph to document");
            }
        }

        document.close();
        return baos.toByteArray();

    }

}
