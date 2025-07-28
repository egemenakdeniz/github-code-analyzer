package org.example.githubfiles.exception.badrequest;

import org.example.githubfiles.exception.BadRequestException;

public class EmptyUsernameException extends BadRequestException {
    public EmptyUsernameException(String message) {
        super(message);    }
}
