package org.example.githubfiles.exception;

public class GithubAuthenticationException extends RuntimeException  implements UserVisibleException{
    public GithubAuthenticationException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 401;
    }
}
