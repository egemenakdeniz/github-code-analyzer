package org.example.githubfiles.exception;

public class EmptyFileListException extends RuntimeException implements UserVisibleException {
    public EmptyFileListException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
