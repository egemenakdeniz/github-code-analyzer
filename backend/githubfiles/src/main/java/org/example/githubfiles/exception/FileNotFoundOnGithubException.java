package org.example.githubfiles.exception;

public class FileNotFoundOnGithubException extends RuntimeException {
    public FileNotFoundOnGithubException(String message) {
        super(message);
    }
}
