package org.example.githubfiles.exception.internal;

import org.example.githubfiles.exception.InternalServerErrorException;

public class AiUnexpectedException extends InternalServerErrorException {
    public AiUnexpectedException(String message) {
        super(message);
    }
}
