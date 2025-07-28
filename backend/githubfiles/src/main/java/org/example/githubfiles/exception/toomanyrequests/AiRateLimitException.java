package org.example.githubfiles.exception.toomanyrequests;

import org.example.githubfiles.exception.TooManyRequestsException;

public class AiRateLimitException extends TooManyRequestsException {
    public AiRateLimitException(String message) {
        super(message);
    }
}
