package org.example.githubfiles.exception;

public abstract class TooManyRequestsException extends ApiException {
    public TooManyRequestsException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 429;
    }
}
