package org.example.githubfiles.exception.unauthorized;

import org.example.githubfiles.exception.UnauthorizedException;

public class GithubAuthenticationException extends UnauthorizedException {
    public GithubAuthenticationException(String message) {
        super(message);
    }
}
