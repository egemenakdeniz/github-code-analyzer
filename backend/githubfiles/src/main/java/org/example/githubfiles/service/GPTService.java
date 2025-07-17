package org.example.githubfiles.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


@Service
public class GPTService {

    private final OpenAiService openAiService;

    public GPTService(@Value("${OPENAI_API_KEY}") String apiKey) {
        //System.out.println(apiKey);
        this.openAiService = new OpenAiService(apiKey);
    }

    public String analyzeWithModel(String inputText) {
        ChatMessage message = new ChatMessage("user", inputText);

        System.out.println("🔍 Prompt: " + inputText);

        ChatCompletionRequest request = ChatCompletionRequest.builder().model("gpt-4o-mini").messages(List.of(message)).temperature(0.2).build();
        return openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
    }
}
