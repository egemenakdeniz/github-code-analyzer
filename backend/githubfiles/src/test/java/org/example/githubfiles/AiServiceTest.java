package org.example.githubfiles;

import org.example.githubfiles.exception.unavailable.AiServiceUnavailableException;
import org.example.githubfiles.service.AiService;
import org.example.githubfiles.exception.badgateway.AiConnectionFailedException;
import org.example.githubfiles.exception.badrequest.UnsupportedProviderException;
import org.example.githubfiles.exception.gatewaytimeout.AiTimeoutException;
import org.example.githubfiles.exception.internal.AiUnexpectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.*;
import java.net.SocketTimeoutException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;



public class AiServiceTest {

    @Mock
    private ChatClient openAiChatClient;

    @Mock
    private ChatClient ollamaChatClient;

    @InjectMocks
    private AiService aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiService = new AiService(openAiChatClient, ollamaChatClient);
        setPrivateField("defaultProvider", "openai");
        setPrivateField("defaultModel", "gpt-3.5-turbo");
    }

    private void setPrivateField(String fieldName, String value) {
        try {
            var field = AiService.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(aiService, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCallOpenAiSuccessfully() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);
        var mockCallResponse = mock(ChatClient.CallResponseSpec.class);

        when(openAiChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);
        when(mockResponse.call()).thenReturn(mockCallResponse);
        when(mockCallResponse.content()).thenReturn("response content");

        String result = aiService.ask("openai", "gpt-4", "Hello");
        assertThat(result).isEqualTo("response content");
    }

    @Test
    void shouldCallOllamaSuccessfully() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);
        var mockCallResponse = mock(ChatClient.CallResponseSpec.class);

        when(ollamaChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);
        when(mockResponse.call()).thenReturn(mockCallResponse);
        when(mockCallResponse.content()).thenReturn("ollama response");

        String result = aiService.ask("ollama", "gemma", "Hi");
        assertThat(result).isEqualTo("ollama response");
    }

    @Test
    void shouldUseDefaultProviderAndModelWhenNull() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);
        var mockCallResponse = mock(ChatClient.CallResponseSpec.class);

        when(openAiChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);
        when(mockResponse.call()).thenReturn(mockCallResponse);
        when(mockCallResponse.content()).thenReturn("default response");

        String result = aiService.ask(null, null, "Hi");
        assertThat(result).isEqualTo("default response");
    }

    @Test
    void shouldThrowWhenProviderIsUnsupported() {
        assertThatThrownBy(() -> aiService.ask("unknown", "model", "msg"))
                .isInstanceOf(UnsupportedProviderException.class);
    }

    @Test
    void shouldThrowTimeoutException() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);
        when(openAiChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);
        when(mockResponse.call()).thenThrow(new ResourceAccessException("timeout", new SocketTimeoutException()));

        assertThatThrownBy(() -> aiService.ask("openai", "model", "prompt"))
                .isInstanceOf(AiTimeoutException.class);
    }


    @Test
    void shouldThrowConnectionFailedOnGenericRestException() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);

        when(openAiChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);
        when(mockResponse.call()).thenThrow(new RestClientException("conn fail"));

        assertThatThrownBy(() -> aiService.ask("openai", "model", "prompt"))
                .isInstanceOf(AiConnectionFailedException.class);
    }

    @Test
    void shouldThrowUnexpectedException() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);
        when(openAiChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);
        when(mockResponse.call()).thenThrow(new RuntimeException("random"));

        assertThatThrownBy(() -> aiService.ask("openai", "model", "prompt"))
                .isInstanceOf(AiUnexpectedException.class);
    }

    @Test
    void shouldThrowServiceUnavailableException() {
        var mockResponse = mock(ChatClient.ChatClientRequestSpec.class);
        when(openAiChatClient.prompt(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.options(any(ChatOptions.class))).thenReturn(mockResponse);

        // create() yöntemi ile ServiceUnavailable hatası üret
        HttpServerErrorException.ServiceUnavailable ex = (HttpServerErrorException.ServiceUnavailable)
                HttpServerErrorException.create(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Service Unavailable",
                        null,
                        null,
                        null
                );

        when(mockResponse.call()).thenThrow(ex);

        assertThatThrownBy(() -> aiService.ask("openai", "model", "prompt"))
                .isInstanceOf(AiServiceUnavailableException.class);
    }

}
