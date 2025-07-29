package org.example.githubfiles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Preview DTO for a generated report, including model used, timestamp, and file path")
public class ReportPreviewDto {

    @Schema(description = "Name of the AI model used for the analysis", example = "gpt-4")
    @NotBlank
    private String modelName;

    @Schema(description = "Timestamp when the report was generated", example = "2025-07-15T14:30:00")
    @NotNull
    private LocalDateTime generatedAt;

    @Schema(description = "ID of the report for downloading", example = "42")
    @NotBlank
    private Long reportId;

}
