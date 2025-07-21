package org.example.githubfiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "DTO indicating whether a specific repository is up-to-date with its GitHub counterpart")
public class RepositoryChangeStatusDto {

    @Schema(description = "GitHub username ", example = "egemenakdeniz")
    @NotBlank
    private String userName;

    @Schema(description = "GitHub repository name ", example = "github-code-analyzer")
    @NotBlank
    private String repoName;

    @Schema(description = "GitHub branch name ", example = "main")
    @NotBlank
    private String branchName;

    @Schema(description = "Indicates if the repository is up-to-date with the latest changes on GitHub", example = "false")
    @NotNull
    private Boolean upToDate;
}
