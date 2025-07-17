package org.example.githubfiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.*;
import org.springframework.web.server.ResponseStatusException;
import org.example.githubfiles.dto.*;
import java.util.Optional;
import java.util.List;

import org.springframework.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Tag(name = "Report Controller", description = "Endpoints for listing and opening generated analysis reports")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private ReportRepository reportRepository;


    @Operation(
            summary = "List reports of a specific repository",
            description = "Returns a list of report previews for the given GitHub repository and branch",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Repository not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportPreviewDto.class))
                    )
            }
    )
    @GetMapping("/of-repo")
    public List<ReportPreviewDto> getReportsByRepo(
            @Parameter(description = "GitHub username", example = "egemenakdeniz") @RequestParam String owner,
            @Parameter(description = "Repository name", example = "github-code-analyzer") @RequestParam String repo,
            @Parameter(description = "Branch name", example = "main") @RequestParam String branch
    ) {
        Optional<Repository> repoOpt = repositoryRepository.findByUserNameAndRepoNameAndBranchName(owner, repo, branch);
        if (repoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found");
        }

        Repository repository = repoOpt.get();

        return reportRepository.findBySession_Repository(repository).stream()
                .map(r -> new ReportPreviewDto(
                        r.getSession().getModel_name(),
                        r.getCreated_at(),
                        r.getPath()
                ))
                .toList();
    }

    @Operation(
            summary = "Open a generated PDF report",
            description = "Returns the binary content of a PDF file given its absolute path",
            responses = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "File not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error while reading file"
                    )
            }
    )
    @GetMapping("/open-pdf")
    public ResponseEntity<byte[]> openPdf(
            @Parameter(description = "Absolute file path of the PDF", example = "/Users/egemen/Desktop/report-42.pdf")
            @RequestParam String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] content = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename(file.getName()).build());

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
