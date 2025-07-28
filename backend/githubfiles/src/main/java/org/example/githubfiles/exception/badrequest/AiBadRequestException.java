package org.example.githubfiles.exception.badrequest;

import org.example.githubfiles.exception.BadRequestException;

public class AiBadRequestException extends BadRequestException {
    public AiBadRequestException(String message) {
        super(message);
    }
}
