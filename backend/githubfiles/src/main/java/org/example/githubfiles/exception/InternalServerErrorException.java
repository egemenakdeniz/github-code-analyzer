package org.example.githubfiles.exception;

public abstract class InternalServerErrorException extends ApiException{
    public InternalServerErrorException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 500;
    }
}
