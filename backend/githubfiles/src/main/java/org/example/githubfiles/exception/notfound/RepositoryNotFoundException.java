package org.example.githubfiles.exception.notfound;

import org.example.githubfiles.exception.NotFoundException;

public class RepositoryNotFoundException extends NotFoundException {
    public RepositoryNotFoundException(String message) {
        super(message);
    }
}
