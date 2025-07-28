package org.example.githubfiles.exception.badrequest;

import org.example.githubfiles.exception.BadRequestException;

public class EmptyPasswordException extends BadRequestException {
    public EmptyPasswordException(String message) {
        super(message);
    }
}
