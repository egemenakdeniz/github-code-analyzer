package org.example.githubfiles.exception;

public abstract class NotFoundException extends ApiException{
    public NotFoundException(String message) {super(message);}
    @Override
    public int getStatusCode() {
        return 404;
    }
}
