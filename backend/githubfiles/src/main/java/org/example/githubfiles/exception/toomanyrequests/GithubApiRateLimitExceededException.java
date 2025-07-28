package org.example.githubfiles.exception.toomanyrequests;

import org.example.githubfiles.exception.TooManyRequestsException;

public class GithubApiRateLimitExceededException extends TooManyRequestsException{
    public GithubApiRateLimitExceededException(String message) {
        super(message);
    }
}
