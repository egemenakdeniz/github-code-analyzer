package org.example.githubfiles.controller;

import org.example.githubfiles.service.OllamaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ollama")
@CrossOrigin(origins = "http://localhost:5173")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/analyze")
    public String analyze(@RequestBody String prompt) {
        return ollamaService.analyzeWithModel(prompt);
    }
}
