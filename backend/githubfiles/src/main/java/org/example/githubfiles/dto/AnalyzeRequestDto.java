package org.example.githubfiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for triggering analysis on a specific GitHub repository using an AI model")
public class AnalyzeRequestDto extends RepositoryImportDto {

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Schema(description = "Name of the AI model to use for code analysis", example = "gpt-4")
    @NotBlank
    private String modelName;
}
