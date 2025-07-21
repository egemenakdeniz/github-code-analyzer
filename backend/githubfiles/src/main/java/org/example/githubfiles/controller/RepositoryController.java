package org.example.githubfiles.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.githubfiles.dto.*;
import org.example.githubfiles.repository.RepositoryRepository;
import org.example.githubfiles.repository.SessionRepository;
import org.example.githubfiles.model.*;
import org.example.githubfiles.service.RepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.service.FileService;
import org.example.githubfiles.service.GithubService;
import org.example.githubfiles.repository.FileRepository;
import jakarta.validation.Valid;

import java.util.*;

@Tag(name = "Repository Controller", description = "Endpoints for importing, updating, and checking GitHub repositories")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/repositories")
public class RepositoryController {


    private final RepositoryService repositoryService;
    private final ModelMapper modelMapper;
    private final RepositoryRepository repositoryRepository;
    private final SessionRepository sessionRepository;
    private final FileService fileService;
    private final GithubService githubService;
    private final FileRepository fileRepository;
    private final EntityManager entityManager;

    //public RepositoryController(RepositoryService repositoryService) {
    //    this.repositoryService = repositoryService;
    //}

    @Operation(
            summary = "Import a repository",
            description = "Imports a GitHub repository and its source files into the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Repository and files saved successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-07-15T22:50:23.317Z",
                      "status": 200,
                      "success": true,
                      "message": "Repository and files saved successfully."
                    }
                """))
                    )
            }
    )
    @PostMapping("/import")
    public ResponseEntity<ApiResponseDto> analyzeRepository(@RequestBody @Valid RepositoryImportDto request) {
        Repository repository = modelMapper.map(request, Repository.class);
        repositoryService.saveRepositoryAndFiles(repository);
        return ResponseEntity.ok(ApiResponseDto.success("Repository and files saved successfully."));
    }

    @Operation(
            summary = "Get all loaded repositories",
            description = "Returns a list of all repositories that have been imported into the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of loaded repositories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RepositoryLoadedDto.class)))
            }
    )
    @GetMapping("/loaded")
    public List<RepositoryLoadedDto> getLoadedRepositories() {
        List<Repository> repos = repositoryRepository.findAll();

        return repos.stream()
                .map(repo -> {
                    RepositoryLoadedDto dto = modelMapper.map(repo, RepositoryLoadedDto.class);
                    dto.setHasAnalysis(sessionRepository.existsByRepository(repo));
                    return dto;
                })
                .toList();
    }


    @Operation(
            summary = "Check which repositories have changed",
            description = "Checks all repositories in the system and marks them as outdated if any changes are detected",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of repositories with change status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RepositoryChangeStatusDto.class)))
            }
    )
    @GetMapping("/has-any-changed")
    public List<RepositoryChangeStatusDto> checkAllRepositoriesForChanges() {
        fileService.checkAndMarkOutdatedRepositories();
        entityManager.clear();

        List<Repository> allRepos = repositoryRepository.findAll();

        return allRepos.stream().map(repo -> {
            RepositoryChangeStatusDto dto = modelMapper.map(repo, RepositoryChangeStatusDto.class);
            dto.setUpToDate(repo.getUpToDate());
            return dto;
        }).toList();
    }

    @Operation(
            summary = "Update an existing repository",
            description = "Fetches the latest files for a given repository and branch, replacing old entries in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Repository updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-07-15T22:55:10.123Z",
                  "status": 200,
                  "success": true,
                  "message": "Repository updated successfully."
                }
                """)
                            )
                    )
            }
    )
    @PostMapping("/update")
    public ResponseEntity<ApiResponseDto> updateRepository(@RequestBody @Valid RepositoryUpdateRequestDto dto) {
        Repository dtoAsEntity = modelMapper.map(dto, Repository.class);

        Optional<Repository> repoOpt = repositoryRepository.findByUserNameAndRepoNameAndBranchName(dtoAsEntity.getUserName(), dtoAsEntity.getRepoName(), dtoAsEntity.getBranchName());
        //if (repoOpt.isEmpty()) {
        //    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repository not found.");
        //}

        Repository repository = repoOpt.get();

        fileRepository.deactivateAllByRepositoryId(repository.getId());

        List<File> newFiles = githubService.fetchFilesFromRepo(dtoAsEntity);
        for (File file : newFiles) {
            file.setRepository(repository);
            file.setActive(true);
            fileRepository.save(file);
        }

        repository.setUpToDate(true);
        repositoryRepository.save(repository);

        return ResponseEntity.ok(ApiResponseDto.success("Repository updated successfully."));
    }
}
