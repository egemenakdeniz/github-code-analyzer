package org.example.githubfiles.controller;

import org.example.githubfiles.service.AnalysisService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/analyze")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping
    public String analyze(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch,
            @RequestParam String model
    ) {
        return analysisService.analyzeRepository(owner, repo, branch,model);
    }
}
