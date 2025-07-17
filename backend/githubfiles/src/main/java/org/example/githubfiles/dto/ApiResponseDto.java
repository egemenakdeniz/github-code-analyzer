package org.example.githubfiles.dto;
import java.time.LocalDateTime;

public class ApiResponseDto {

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public boolean isSuccess() {return success;}

    public void setSuccess(boolean success) {this.success = success;}

    public int getStatus() {return status;}

    public void setStatus(int status) {this.status = status;}

    public LocalDateTime getTimestamp() {return timestamp;}

    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}

    private LocalDateTime timestamp;
    private int status;
    private boolean success;
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
