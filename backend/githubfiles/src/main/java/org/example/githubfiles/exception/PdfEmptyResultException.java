package org.example.githubfiles.exception;

public class PdfEmptyResultException extends RuntimeException implements UserVisibleException{
    public PdfEmptyResultException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 204;
    }
}
