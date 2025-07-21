package org.example.githubfiles.exception;

public class GithubApiRateLimitExceededException extends RuntimeException  implements UserVisibleException{
    public GithubApiRateLimitExceededException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 429;
    }
}
