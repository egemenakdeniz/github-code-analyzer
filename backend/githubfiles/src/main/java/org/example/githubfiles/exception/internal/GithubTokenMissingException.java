package org.example.githubfiles.exception.internal;

import org.example.githubfiles.exception.InternalServerErrorException;

public class GithubTokenMissingException extends InternalServerErrorException {
    public GithubTokenMissingException(String message) {
        super(message);
    }
}
