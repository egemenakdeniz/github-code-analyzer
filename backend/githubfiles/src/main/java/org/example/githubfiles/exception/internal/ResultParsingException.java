package org.example.githubfiles.exception.internal;

import org.example.githubfiles.exception.InternalServerErrorException;


public class ResultParsingException extends InternalServerErrorException {
    public ResultParsingException(String message) {
        super(message);
    }
}
