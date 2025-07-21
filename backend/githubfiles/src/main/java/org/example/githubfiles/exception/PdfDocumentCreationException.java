package org.example.githubfiles.exception;

public class PdfDocumentCreationException extends RuntimeException implements UserVisibleException{
    public PdfDocumentCreationException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 500;
    }
}
