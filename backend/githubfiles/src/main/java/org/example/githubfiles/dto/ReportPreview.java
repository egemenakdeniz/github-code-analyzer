package org.example.githubfiles.dto;

import java.time.LocalDateTime;

public class ReportPreview {
    private String modelName;
    private LocalDateTime generatedAt;
    private String filePath;

    public ReportPreview(String modelName, LocalDateTime generatedAt, String filePath) {
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
