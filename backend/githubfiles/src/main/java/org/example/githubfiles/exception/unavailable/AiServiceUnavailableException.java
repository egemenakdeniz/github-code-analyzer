package org.example.githubfiles.exception.unavailable;

import org.example.githubfiles.exception.ServiceUnavailableException;

public class AiServiceUnavailableException extends ServiceUnavailableException {
    public AiServiceUnavailableException(String message) {
        super(message);
    }
}
