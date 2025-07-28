package org.example.githubfiles.exception.badrequest;

import org.example.githubfiles.exception.BadRequestException;

public class UnsupportedProviderException extends BadRequestException {
    public UnsupportedProviderException(String message) {
        super(message);
    }
}
