package org.example.githubfiles.exception.notfound;

import org.example.githubfiles.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
