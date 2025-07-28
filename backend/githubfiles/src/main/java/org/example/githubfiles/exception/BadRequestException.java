package org.example.githubfiles.exception;

public abstract class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
