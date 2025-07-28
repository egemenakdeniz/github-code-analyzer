package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class EmptyAiResponseException extends BadGatewayException {
    public EmptyAiResponseException(String message) {super(message);}
}
