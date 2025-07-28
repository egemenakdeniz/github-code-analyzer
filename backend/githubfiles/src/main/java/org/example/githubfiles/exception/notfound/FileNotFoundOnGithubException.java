package org.example.githubfiles.exception.notfound;

import org.example.githubfiles.exception.NotFoundException;

public class FileNotFoundOnGithubException extends NotFoundException {
    public FileNotFoundOnGithubException(String message) {
        super(message);
    }
}
