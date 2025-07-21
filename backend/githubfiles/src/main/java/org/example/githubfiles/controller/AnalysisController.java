package org.example.githubfiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.githubfiles.dto.AnalyzeRequestDto;
import org.example.githubfiles.dto.ApiResponseDto;
import org.example.githubfiles.service.AnalysisService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.model.*;

@Tag(name = "Analysis Controller", description = "Endpoints for triggering AI-based code analysis on repositories")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analyze")
public class AnalysisController {

    private final AnalysisService analysisService;

    private final ModelMapper modelMapper;

    @Operation(
            summary = "Analyze a GitHub repository using an AI model",
            description = "Triggers static code analysis on a given GitHub repository and branch using the selected AI model.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Analysis completed successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(value = """
                           {
                               "timestamp": "2025-07-15T22:50:23.317Z",
                               "status": 200,
                               "success": true,
                               "message": "Analysis completed successfully"
                           }
                       """))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponseDto> analyze(@RequestBody AnalyzeRequestDto requestDto) {
        Repository repository = modelMapper.map(requestDto, Repository.class);
        String model = requestDto.getModelName();
        String result = analysisService.analyzeRepository(repository , model);
        System.out.println(result);
        return ResponseEntity.ok(ApiResponseDto.success("Analysis completed successfully"));
    }
}
