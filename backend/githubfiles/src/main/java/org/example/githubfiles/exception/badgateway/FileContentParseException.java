package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class FileContentParseException extends BadGatewayException {
    public FileContentParseException(String message) {
        super(message);
    }
}
