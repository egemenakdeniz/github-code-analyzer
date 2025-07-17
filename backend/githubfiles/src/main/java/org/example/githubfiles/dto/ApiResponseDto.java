package org.example.githubfiles.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard API response wrapper containing status, success flag, and message")
public class ApiResponseDto {

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public boolean isSuccess() {return success;}

    public void setSuccess(boolean success) {this.success = success;}

    public int getStatus() {return status;}

    public void setStatus(int status) {this.status = status;}

    public LocalDateTime getTimestamp() {return timestamp;}

    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}

    @Schema(description = "Timestamp when the response was generated", example = "2025-07-15T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code representing the result of the operation", example = "200")
    private int status;

    @Schema(description = "Indicates whether the operation was successful", example = "True")
    private boolean success;

    @Schema(description = "Human-readable message describing the result of the operation", example = "Analysis completed successfully")
    private String message;

    public ApiResponseDto(boolean success, int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static ApiResponseDto failure(int status, String message) {
        return new ApiResponseDto(false, status, message);
    }

    public static ApiResponseDto success(String message) {
        return new ApiResponseDto(true, 200, message);
    }

}
