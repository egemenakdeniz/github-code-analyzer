package org.example.githubfiles.exception.badrequest;

import org.example.githubfiles.exception.BadRequestException;

public class InvalidDateFormatException extends BadRequestException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}
