package org.example.githubfiles.config;

import org.example.githubfiles.dto.ApiResponseDto;
import org.example.githubfiles.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Tüm custom exception'ları yakalar
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseDto> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ApiResponseDto.failure(ex.getStatusCode(), ex.getMessage()));
    }

/*
    //Global Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleGeneric(Exception ex) {
        if (ex instanceof UserVisibleException userVisible) {
            log.error("User-visible exception occurred: [{}] - {}", ex.getClass().getSimpleName(), ex.getMessage());
            return ResponseEntity.status(userVisible.getStatusCode())
                    .body(ApiResponseDto.failure(userVisible.getStatusCode(), ex.getMessage()));
        } else {
            log.error("Unhandled internal exception: [{}] - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.failure(500, "Sunucuda beklenmeyen bir hata oluştu."));
        }
    }

 */

}
