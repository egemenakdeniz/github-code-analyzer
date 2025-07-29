package org.example.githubfiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.githubfiles.exception.badgateway.AiConnectionFailedException;
import org.example.githubfiles.exception.badrequest.AiBadRequestException;
import org.example.githubfiles.exception.badrequest.UnsupportedProviderException;
import org.example.githubfiles.exception.gatewaytimeout.AiTimeoutException;
import org.example.githubfiles.exception.internal.AiUnexpectedException;
import org.example.githubfiles.exception.toomanyrequests.AiRateLimitException;
import org.example.githubfiles.exception.unavailable.AiServiceUnavailableException;
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
        log.debug("Kullanıcı provider: {}, model: {}", provider, model);
        log.debug("Kullanılan provider: {}, model: {}", finalProvider, finalModel);

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
            default -> throw new UnsupportedProviderException("Unsupported provider: " + provider);
        }

        Prompt prompt = new Prompt(List.of(new UserMessage(promptText)));

        try {
            return chatClient
                    .prompt(prompt)
                    .options(options)
                    .call()
                    .content();
        }catch (org.springframework.web.client.ResourceAccessException ex) {
            if (ex.getCause() instanceof java.net.SocketTimeoutException) {
                log.error("AI çağrısı zaman aşımına uğradı", ex);
                throw new AiTimeoutException("The AI request timed out.");
            }
            log.error("AI bağlantı hatası", ex);
            throw new AiConnectionFailedException("Failed to connect to AI provider.");
        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests ex) {
            log.error("AI servis sınırı aşıldı (rate limit)", ex);
            throw new AiRateLimitException("Rate limit exceeded on the AI provider.");

        } catch (org.springframework.web.client.HttpClientErrorException.BadRequest ex) {
            log.error("AI geçersiz istekle reddetti", ex);
            throw new AiBadRequestException("Invalid request sent to the AI provider.");

        } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable ex) {
            log.error("AI servisi geçici olarak kullanılamıyor", ex);
            throw new AiServiceUnavailableException("AI provider is temporarily unavailable.");

        } catch (org.springframework.web.client.RestClientException ex) {
            log.error("AI bağlantı kurulamadı", ex);
            throw new AiConnectionFailedException("Failed to connect to AI provider.");

        }
        catch (Exception ex) {
            log.error("AI çağrısı sırasında beklenmeyen bir hata oluştu", ex);
            throw new AiUnexpectedException("An unexpected error occurred while calling the AI model.");
        }
    }
}