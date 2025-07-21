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

/*
    // === GITHUB Exceptions ===
    @ExceptionHandler(RepositoryAlreadyExistException.class)
    public ResponseEntity<ApiResponseDto> handleRepositoryAlreadyExist(@NotNull RepositoryAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.failure(409, ex.getMessage()));
    }
    @ExceptionHandler(GithubTokenMissingException.class)
    public ResponseEntity<ApiResponseDto> githubTokenMissing(@NotNull GithubTokenMissingException ex) {
        log.error("GITHUB TOKEN MISSING: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.failure(500, "Sunucu yapılandırma hatası. Lütfen daha sonra tekrar deneyin."));
    }
    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ApiResponseDto> repositoryNotFound(@NotNull RepositoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.failure(404, ex.getMessage()));
    }
    @ExceptionHandler(InvalidRepositoryMetadataException.class)
    public ResponseEntity<ApiResponseDto> repositoryNullExcepiton(@NotNull InvalidRepositoryMetadataException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.failure(400, ex.getMessage()));
    }
    @ExceptionHandler(GithubServiceException.class)
    public ResponseEntity<ApiResponseDto> handleGithubServiceException(@NotNull GithubServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponseDto.failure(502, ex.getMessage()));
    }
    @ExceptionHandler(GithubApiRateLimitExceededException.class)
    public ResponseEntity<ApiResponseDto> githubApiRateLimitExceededException(@NotNull GithubApiRateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponseDto.failure(429, ex.getMessage()));
    }
    @ExceptionHandler(MalformedGithubApiResponseException.class)
    public ResponseEntity<ApiResponseDto> malformedGithubApiResponseException(@NotNull MalformedGithubApiResponseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponseDto.failure(502, ex.getMessage()));
    }


    @ExceptionHandler(GithubAuthenticationException.class)
    public ResponseEntity<ApiResponseDto> githubAuthenticationException(@NotNull GithubAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.failure(401, ex.getMessage()));
    }



    // === FILE Exceptions ===
    @ExceptionHandler(FileContentFetchException.class)
    public ResponseEntity<ApiResponseDto> fileContentFetchException(@NotNull FileContentFetchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponseDto.failure(502, ex.getMessage()));
    }
    @ExceptionHandler(FileNotFoundOnGithubException.class)
    public ResponseEntity<ApiResponseDto> fileNotFoundOnGithubException(@NotNull FileNotFoundOnGithubException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.failure(404, ex.getMessage()));
    }
    @ExceptionHandler(FileContentMissingException.class)
    public ResponseEntity<ApiResponseDto> fileContentMissingException(@NotNull FileContentMissingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponseDto.failure(502, ex.getMessage()));
    }
    @ExceptionHandler(FileContentParseException.class)
    public ResponseEntity<ApiResponseDto> fileContentParseException(@NotNull FileContentParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponseDto.failure(502, ex.getMessage()));
    }
    @ExceptionHandler(EmptyFileListException.class)
    public ResponseEntity<ApiResponseDto> handleEmptyFileListException(@NotNull EmptyFileListException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.failure(400, ex.getMessage()));
    }

    // === PDF Exceptions ===
    @ExceptionHandler(PdfEmptyResultException.class)
    public ResponseEntity<ApiResponseDto> handlePdfEmptyResultException(@NotNull PdfEmptyResultException ex) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponseDto.failure(204, ex.getMessage()));
    }
    @ExceptionHandler(PdfFontLoadException.class)
    public ResponseEntity<ApiResponseDto> handlePdfFontLoadException(@NotNull PdfFontLoadException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.failure(500, ex.getMessage()));
    }
    @ExceptionHandler(PdfDocumentCreationException.class)
    public ResponseEntity<ApiResponseDto> handlePdfDocumentCreationException(@NotNull PdfDocumentCreationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.failure(500, ex.getMessage()));
    }

    //GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleUnexpected(Exception ex) {
        log.error("Unhandled exception caught: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.failure(500, "Sunucuda beklenmeyen bir hata oluştu."));
    }
    */
}
