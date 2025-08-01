package org.example.githubfiles.controller;

import lombok.RequiredArgsConstructor;
import org.example.githubfiles.dto.ModelsResponseDto;
import org.example.githubfiles.service.ModelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    @GetMapping("/openai")
    public ResponseEntity<ModelsResponseDto> getFilteredModelIds() {
        ModelsResponseDto dto = modelService.getOpenAiModels();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/ollama")
    public ResponseEntity<ModelsResponseDto> getOllamaModels() {
        ModelsResponseDto dto = modelService.getOllamaModels();
        return ResponseEntity.ok(dto);
    }
}