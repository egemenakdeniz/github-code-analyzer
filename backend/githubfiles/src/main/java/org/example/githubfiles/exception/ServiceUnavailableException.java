package org.example.githubfiles.exception;

public abstract class ServiceUnavailableException extends ApiException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 503;
    }
}
