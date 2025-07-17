package org.example.githubfiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Preview DTO for a generated report, including model used, timestamp, and file path")
public class ReportPreviewDto {

    @Schema(description = "Name of the AI model used for the analysis", example = "gpt-4")
    @NotBlank
    private String modelName;

    @Schema(description = "Timestamp when the report was generated", example = "2025-07-15T14:30:00")
    @NotNull
    private LocalDateTime generatedAt;

    @Schema(description = "Path of the generated PDF report file", example = "/api/reports/open-pdf?path=/reports/report-42.pdf")
    @NotBlank
    private String filePath;

    public ReportPreviewDto(String modelName, LocalDateTime generatedAt, String filePath) {
        this.modelName = modelName;
        this.generatedAt = generatedAt;
        this.filePath = filePath;
    }

    public String getModelName() {
        return modelName;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getFilePath() {
        return filePath;
    }
}
