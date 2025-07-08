package org.example.githubfiles.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ollamaApiUrl = "http://localhost:11434/api/generate";

    public String analyzeWithModel(String inputText) {
        try {
            String requestJson = "{ \"model\": \"gemma3:4b\", \"prompt\": \"" + escapeJson(inputText) + "\" }";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(ollamaApiUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                //System.out.println(response.getBody());
                return response.getBody();
            } else {
                return "Ollama API error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
