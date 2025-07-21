package org.example.githubfiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "DTO representing a previously loaded repository along with its analysis status and internal ID")
public class RepositoryLoadedDto {

    @Schema(description = "GitHub username ", example = "egemenakdeniz")
    @NotBlank
    private String userName;


    @Schema(description = "GitHub repository name ", example = "github-code-analyzer")
    @NotBlank
    private String repoName;

    @Schema(description = "GitHub branch name ", example = "main")
    @NotBlank
    private String branchName;

    @Schema(description = "Internal ID of the repository in the system", example = "42")
    @NotNull
    private Long id;


    @Schema(description = "Indicates whether the repository has been analyzed", example = "true")
    @NotNull
    private boolean hasAnalysis;




}
