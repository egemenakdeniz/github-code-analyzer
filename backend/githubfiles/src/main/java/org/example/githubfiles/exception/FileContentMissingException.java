package org.example.githubfiles.exception;

public class FileContentMissingException extends RuntimeException {
    public FileContentMissingException(String message) {
        super(message);
    }
}
