package org.example.githubfiles.exception;

public class MalformedGithubApiResponseException extends RuntimeException {
    public MalformedGithubApiResponseException(String message) {
        super(message);
    }

}
