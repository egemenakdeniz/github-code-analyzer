package org.example.githubfiles.exception.conflict;

import org.example.githubfiles.exception.ConflictException;

public class UsernameAlreadyExistsException extends ConflictException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
