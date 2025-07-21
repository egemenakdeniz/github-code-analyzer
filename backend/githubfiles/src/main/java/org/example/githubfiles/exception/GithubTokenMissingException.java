package org.example.githubfiles.exception;

public class GithubTokenMissingException extends RuntimeException {
    public GithubTokenMissingException(String message) {
        super(message);
    }
}
