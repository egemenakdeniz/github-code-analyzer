package org.example.githubfiles.dto;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for triggering analysis on a specific GitHub repository using an AI model")
public class AnalyzeRequestDto{

    @Schema(description = "GitHub username ", example = "egemenakdeniz")
    @NotBlank
    private String userName;

    @Schema(description = "GitHub repository name ", example = "github-code-analyzer")
    @NotBlank
    private String repoName;

    @Schema(description = "GitHub branch name ", example = "main")
    @NotBlank
    private String branchName;

    @Schema(description = "AI provider to use (e.g., openai, ollama)", example = "openai")
    private String providerName;

    @Schema(description = "Name of the AI model to use for code analysis", example = "gpt-4")
    private String modelName;
}
