package org.example.githubfiles.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "Repository import request payload")
public class RepositoryImportDto {

    @Schema(description = "GitHub username ", example = "egemenakdeniz")
    @NotBlank
    private String userName;


    @Schema(description = "GitHub repository name ", example = "github-code-analyzer")
    @NotBlank
    private String repoName;

    @Schema(description = "GitHub branch name ", example = "main")
    @NotBlank
    private String branchName;

}
