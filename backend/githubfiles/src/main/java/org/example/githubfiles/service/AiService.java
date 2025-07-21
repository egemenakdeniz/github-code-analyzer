package org.example.githubfiles.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    @Qualifier("openAiChatClient")
    private final ChatClient openAiChatClient;

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaChatClient;

    public String ask(String provider, String prompt) {
        ChatClient chatClient;

        switch (provider.toLowerCase()) {
            case "openai" -> chatClient = openAiChatClient;
            case "ollama" -> chatClient = ollamaChatClient;
            default -> throw new IllegalArgumentException("Unsupported AI provider: " + provider);
        }

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}