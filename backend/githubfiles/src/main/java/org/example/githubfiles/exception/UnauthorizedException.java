package org.example.githubfiles.exception;

public abstract class UnauthorizedException extends ApiException{
    public UnauthorizedException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 401;
    }
}
