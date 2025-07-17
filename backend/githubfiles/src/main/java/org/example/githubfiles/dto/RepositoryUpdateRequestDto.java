package org.example.githubfiles.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating an existing GitHub repository's information")
public class RepositoryUpdateRequestDto extends RepositoryImportDto{
}
