package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class FileContentFetchException extends BadGatewayException {
    public FileContentFetchException(String message) {
        super(message);
    }
}
