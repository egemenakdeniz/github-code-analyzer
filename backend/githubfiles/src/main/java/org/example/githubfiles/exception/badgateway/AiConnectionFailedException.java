package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class AiConnectionFailedException extends BadGatewayException {
    public AiConnectionFailedException(String message) {
        super(message);
    }
}
