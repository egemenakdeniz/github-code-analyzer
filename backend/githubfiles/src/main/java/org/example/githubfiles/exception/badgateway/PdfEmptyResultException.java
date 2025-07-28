package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class PdfEmptyResultException extends BadGatewayException {
    public PdfEmptyResultException(String message) {
        super(message);
    }
}
