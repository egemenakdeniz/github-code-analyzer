package org.example.githubfiles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.model.Repository;
import org.example.githubfiles.repository.*;
import org.springframework.web.server.ResponseStatusException;
import org.example.githubfiles.dto.*;
import java.util.Optional;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private ReportRepository reportRepository;


    @GetMapping("/of-repo")
    public List<ReportPreview> getReportsByRepo(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch
    ) {
        Optional<Repository> repoOpt = repositoryRepository.findByUserNameAndRepoNameAndBranchName(owner, repo, branch);
        if (repoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository not found");
        }

        Repository repository = repoOpt.get();

        return reportRepository.findBySession_Repository(repository).stream()
                .map(r -> new ReportPreview(
                        r.getSession().getModel_name(),
                        r.getCreated_at(),
                        r.getPath()
                ))
                .toList();
    }

    @GetMapping("/open-pdf")
    public ResponseEntity<byte[]> openPdf(@RequestParam String path) {
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
