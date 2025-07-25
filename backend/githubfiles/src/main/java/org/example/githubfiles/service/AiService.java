package org.example.githubfiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.prompt.Prompt;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {


    @Value("${ai.default.provider}")
    private String defaultProvider;

    @Value("${ai.default.model}")
    private String defaultModel;

    @Qualifier("openAiChatClient")
    private final ChatClient openAiChatClient;

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaChatClient;

    public String ask(String provider,String model, String promptText) {
        ChatClient chatClient;
        ChatOptions options;
        String finalProvider;
        String finalModel;
        log.debug(model);

        if (provider == null || provider.isBlank()) {
            finalProvider = defaultProvider;
            finalModel = defaultModel;
        } else {
            finalProvider = provider;
            finalModel = (model == null || model.isBlank()) ? defaultModel : model;
        }

        switch (finalProvider.toLowerCase()) {
            case "openai" -> {
                chatClient = openAiChatClient;
                options = OpenAiChatOptions.builder()
                        .model(finalModel)
                        .build();
            }
            case "ollama" -> {
                chatClient = ollamaChatClient;
                options = OllamaOptions.builder()
                        .model(finalModel)
                        .build();
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        }

        Prompt prompt = new Prompt(List.of(new UserMessage(promptText)));

        return chatClient
                .prompt(prompt)
                .options(options)
                .call()
                .content();
    }
}