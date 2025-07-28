package org.example.githubfiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.githubfiles.dto.AnalyzeRequestDto;
import org.example.githubfiles.dto.ApiResponseDto;
import org.example.githubfiles.service.AnalysisService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.model.*;

@Slf4j
@Tag(name = "Analysis Controller", description = "Endpoints for triggering AI-based code analysis on repositories")
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
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request (e.g., empty input or unsupported provider)",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = {
                                            @ExampleObject(name = "Empty or Invalid Input", value = """
                        {
                            "timestamp": "2025-07-15T22:51:00.317Z",
                            "status": 400,
                            "success": false,
                            "message": "The AI response is empty or invalid."
                        }
                    """),
                                            @ExampleObject(name = "Unsupported Provider", value = """
                        {
                            "timestamp": "2025-07-15T22:51:10.317Z",
                            "status": 400,
                            "success": false,
                            "message": "Unsupported provider: huggingface"
                        }
                    """)
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - analysis already in progress",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Analysis In Progress", value = """
                    {
                        "timestamp": "2025-07-15T22:51:20.317Z",
                        "status": 403,
                        "success": false,
                        "message": "Analysis is already in progress."
                    }
                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found (e.g., user or repository)",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = {
                                            @ExampleObject(name = "Repository Not Found", value = """
                        {
                            "timestamp": "2025-07-15T22:51:30.317Z",
                            "status": 404,
                            "success": false,
                            "message": "Repository not found during analysis."
                        }
                    """),
                                            @ExampleObject(name = "User Not Found", value = """
                        {
                            "timestamp": "2025-07-15T22:51:35.317Z",
                            "status": 404,
                            "success": false,
                            "message": "Kullanıcı bulunamadı: egemen"
                        }
                    """)
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "408",
                            description = "Request Timeout - AI call took too long",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Timeout", value = """
                    {
                        "timestamp": "2025-07-15T22:51:40.317Z",
                        "status": 408,
                        "success": false,
                        "message": "The AI request timed out."
                    }
                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - username exists or invalid file path",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = {
                                            @ExampleObject(name = "Invalid File Path", value = """
                        {
                            "timestamp": "2025-07-15T22:51:50.317Z",
                            "status": 409,
                            "success": false,
                            "message": "The AI returned a file path that does not exist: src/ghost/File.java"
                        }
                    """),
                                            @ExampleObject(name = "Username Exists", value = """
                        {
                            "timestamp": "2025-07-15T22:51:55.317Z",
                            "status": 409,
                            "success": false,
                            "message": "Username already exists: egemen"
                        }
                    """)
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "429",
                            description = "Too Many Requests - AI rate limit exceeded",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Rate Limit Exceeded", value = """
                    {
                        "timestamp": "2025-07-15T22:52:00.317Z",
                        "status": 429,
                        "success": false,
                        "message": "Rate limit exceeded on the AI provider."
                    }
                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = {
                                            @ExampleObject(name = "PDF Error", value = """
                        {
                            "timestamp": "2025-07-15T22:52:10.317Z",
                            "status": 500,
                            "success": false,
                            "message": "PDF oluşturulamadı: Font yüklenemedi"
                        }
                    """),
                                            @ExampleObject(name = "Parsing Error", value = """
                        {
                            "timestamp": "2025-07-15T22:52:15.317Z",
                            "status": 500,
                            "success": false,
                            "message": "An error occurred while processing the AI response."
                        }
                    """),
                                            @ExampleObject(name = "Unexpected AI Error", value = """
                        {
                            "timestamp": "2025-07-15T22:52:20.317Z",
                            "status": 500,
                            "success": false,
                            "message": "An unexpected error occurred while calling the AI model."
                        }
                    """)
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable - AI service or internet issue",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = {
                                            @ExampleObject(name = "AI Unavailable", value = """
                        {
                            "timestamp": "2025-07-15T22:52:25.317Z",
                            "status": 503,
                            "success": false,
                            "message": "AI provider is temporarily unavailable."
                        }
                    """),
                                            @ExampleObject(name = "No Internet", value = """
                        {
                            "timestamp": "2025-07-15T22:52:30.317Z",
                            "status": 503,
                            "success": false,
                            "message": "The server is not connected to the internet. Please check your network connection."
                        }
                    """),
                                            @ExampleObject(name = "Connection Failed", value = """
                        {
                            "timestamp": "2025-07-15T22:52:35.317Z",
                            "status": 503,
                            "success": false,
                            "message": "Failed to connect to AI provider."
                        }
                    """)
                                    }
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponseDto> analyze(@RequestBody AnalyzeRequestDto requestDto) {

        Repository repository = modelMapper.map(requestDto, Repository.class);
        String model = requestDto.getModelName();
        String providerName = requestDto.getProviderName();
        analysisService.analyzeRepository(repository,providerName,model);
        return ResponseEntity.ok(ApiResponseDto.success("Analysis completed successfully"));
    }
}
