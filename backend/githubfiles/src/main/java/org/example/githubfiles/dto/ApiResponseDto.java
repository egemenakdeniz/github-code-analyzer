package org.example.githubfiles.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Schema(description = "Standard API response wrapper containing status, success flag, and message")
public class ApiResponseDto {

    @Builder.Default
    @Schema(description = "Timestamp when the response was generated", example = "2025-07-15T14:30:00")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "HTTP status code representing the result of the operation", example = "200")
    private int status;

    @Schema(description = "Indicates whether the operation was successful", example = "True")
    private boolean success;

    @Schema(description = "Human-readable message describing the result of the operation", example = "Analysis completed successfully")
    private String message;

/*
    public ApiResponseDto(boolean success, int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.success = success;
        this.message = message;
    }
*/

    public static ApiResponseDto success(String message) {
        return ApiResponseDto.builder()
                .success(true)
                .status(200)
                .message(message)
                .build();
    }

    public static ApiResponseDto failure(int status, String message) {
        return ApiResponseDto.builder()
                .success(false)
                .status(status)
                .message(message)
                .build();
    }

}
