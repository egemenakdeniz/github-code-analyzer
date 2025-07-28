package org.example.githubfiles.exception;

public abstract class GatewayTimeoutException extends ApiException{
    public GatewayTimeoutException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 504;
    }

}
