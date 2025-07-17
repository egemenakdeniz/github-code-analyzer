package org.example.githubfiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "DTO representing a previously loaded repository along with its analysis status and internal ID")
public class RepositoryLoadedDto extends RepositoryImportDto {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "Internal ID of the repository in the system", example = "42")
    @NotNull
    private Long id;

    public boolean isHasAnalysis() {
        return hasAnalysis;
    }

    public void setHasAnalysis(boolean hasAnalysis) {
        this.hasAnalysis = hasAnalysis;
    }

    @Schema(description = "Indicates whether the repository has been analyzed", example = "true")
    @NotNull
    private boolean hasAnalysis;




}
