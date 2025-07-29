package org.example.githubfiles.config;

import org.example.githubfiles.dto.ApiResponseDto;
import org.example.githubfiles.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseDto> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ApiResponseDto.failure(ex.getStatusCode(), ex.getMessage()));
    }

}
