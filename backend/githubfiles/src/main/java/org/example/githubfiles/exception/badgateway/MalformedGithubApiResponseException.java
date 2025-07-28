package org.example.githubfiles.exception.badgateway;

import org.example.githubfiles.exception.BadGatewayException;

public class MalformedGithubApiResponseException extends BadGatewayException {
    public MalformedGithubApiResponseException(String message) {
        super(message);
    }

}
