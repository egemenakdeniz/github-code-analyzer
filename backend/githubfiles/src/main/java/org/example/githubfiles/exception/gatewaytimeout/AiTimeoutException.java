package org.example.githubfiles.exception.gatewaytimeout;

import org.example.githubfiles.exception.GatewayTimeoutException;

public class AiTimeoutException extends GatewayTimeoutException {
    public AiTimeoutException(String message) {
        super(message);
    }
}
