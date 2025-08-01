package org.example.githubfiles.service;

import lombok.RequiredArgsConstructor;
import org.example.githubfiles.dto.ModelsResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ModelsResponseDto getOpenAiModels() {
        String url = "https://api.openai.com/v1/models";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openAiApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            List<Map<String, Object>> models = (List<Map<String, Object>>) response.getBody().get("data");

            List<String> modelIds = models.stream()
                    .map(m -> (String) m.get("id"))
                    .filter(id ->
                            id.startsWith("gpt-") &&
                                    !id.contains("audio") &&
                                    !id.contains("tts") &&
                                    !id.contains("image") &&
                                    !id.contains("transcribe") &&
                                    !id.contains("search") &&
                                    !id.contains("moderation") &&
                                    !id.contains("embedding") &&
                                    !id.contains("whisper") &&
                                    !id.contains("dall-e")
                    )
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            return new ModelsResponseDto(modelIds);

        } catch (Exception e) {
            return new ModelsResponseDto(List.of("OpenAI modelleri alınamadı: " + e.getMessage()));
        }
    }

    public ModelsResponseDto getOllamaModels() {
        String url = "http://localhost:11434/api/tags";

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            List<Map<String, Object>> models = (List<Map<String, Object>>) response.getBody().get("models");

            List<String> modelNames = models.stream()
                    .map(m -> (String) m.get("name"))
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());

            return new ModelsResponseDto(modelNames);

        } catch (Exception e) {
            return new ModelsResponseDto(List.of("Ollama modelleri alınamadı: " + e.getMessage()));
        }
    }
}