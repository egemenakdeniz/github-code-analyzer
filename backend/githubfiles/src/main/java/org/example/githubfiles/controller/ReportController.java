package org.example.githubfiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.repository.*;
import org.example.githubfiles.dto.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.http.*;


@Tag(name = "Report Controller", description = "Endpoints for listing and opening generated analysis reports")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportRepository reportRepository;

    @Operation(
            summary = "List reports of a specific repository",
            description = "Returns a list of report previews for the given GitHub repository and branch",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "owner", description = "GitHub username", example = "egemenakdeniz"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "repo", description = "Repository name", example = "github-code-analyzer"),
                    @io.swagger.v3.oas.annotations.Parameter(name = "branch", description = "Branch name", example = "main")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success")
            }
    )
    @GetMapping("/of-repo")
    public List<ReportPreviewDto> getReportsByRepo(
            @RequestParam String owner,
            @RequestParam String repo,
           @RequestParam String branch
    ) {
        return reportRepository.findReportPreviewRaw(owner, repo, branch).stream()
                .map(row -> {
                    Long reportId = ((Number) row[0]).longValue(); // ID
                    String modelName = (String) row[1];             // model_name
                    LocalDateTime generatedAt;

                    if (row[2] instanceof java.sql.Timestamp ts) {
                        generatedAt = ts.toLocalDateTime();
                    } else if (row[2] instanceof java.time.Instant instant) {
                        generatedAt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
                    } else if (row[2] instanceof java.time.LocalDateTime ldt) {
                        generatedAt = ldt;
                    } else {
                        throw new IllegalArgumentException("Unexpected datetime type: " + row[2].getClass());
                    }

                    return ReportPreviewDto.builder()
                            .reportId(reportId)
                            .modelName(modelName)
                            .generatedAt(generatedAt)
                            .build();
                })
                .toList();
    }

    @Operation(
            summary = "Open a generated PDF report",
            description = "Returns the binary content of a PDF file given its absolute path",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "reportId",
                            description = "Report ID",
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF file returned successfully",
                            content = @Content(
                                    mediaType = "application/pdf",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    )
            }
    )
    @GetMapping("/open-pdf")
    public ResponseEntity<byte[]> openPdf(
            @RequestParam Long reportId) {

        byte[] content = reportRepository.findFileDataById(reportId);

        if (content == null) {
            return ResponseEntity.notFound().build();
        }

        //System.out.println("Çalışıyor");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("report-" + reportId + ".pdf").build());

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
