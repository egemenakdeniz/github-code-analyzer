package org.example.githubfiles.exception.unauthorized;

import org.example.githubfiles.exception.UnauthorizedException;

public class InvalidRefreshTokenException extends UnauthorizedException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
