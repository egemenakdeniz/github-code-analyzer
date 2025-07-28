package org.example.githubfiles.exception;

public abstract class BadGatewayException extends ApiException {
    public BadGatewayException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 502;
    }
}
