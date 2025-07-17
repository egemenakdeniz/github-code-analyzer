package org.example.githubfiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "DTO indicating whether a specific repository is up-to-date with its GitHub counterpart")
public class RepositoryChangeStatusDto extends RepositoryImportDto{

    public Boolean getUpToDate() {
        return upToDate;
    }

    public void setUpToDate(Boolean upToDate) {
        this.upToDate = upToDate;
    }

    @Schema(description = "Indicates if the repository is up-to-date with the latest changes on GitHub", example = "false")
    @NotNull
    private Boolean upToDate;
}
