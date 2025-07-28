package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class FileContentMissingException extends BadGatewayException {
    public FileContentMissingException(String message) {
        super(message);
    }
}
