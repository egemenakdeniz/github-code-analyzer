package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class GithubServiceException extends BadGatewayException {
    public GithubServiceException(String message) {
        super(message);
    }
}
