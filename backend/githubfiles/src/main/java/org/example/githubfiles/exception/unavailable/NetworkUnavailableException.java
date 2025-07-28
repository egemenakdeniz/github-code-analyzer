package org.example.githubfiles.exception.unavailable;

import org.example.githubfiles.exception.ServiceUnavailableException;

public class NetworkUnavailableException extends ServiceUnavailableException {
    public NetworkUnavailableException(String message) {
        super(message);
    }
}
