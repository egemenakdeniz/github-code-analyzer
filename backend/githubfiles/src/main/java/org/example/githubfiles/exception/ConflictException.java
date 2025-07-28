package org.example.githubfiles.exception;

public abstract class ConflictException extends ApiException{
    public ConflictException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 409;
    }
}
