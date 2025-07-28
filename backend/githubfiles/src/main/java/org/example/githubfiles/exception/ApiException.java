package org.example.githubfiles.exception;

public abstract class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
}
