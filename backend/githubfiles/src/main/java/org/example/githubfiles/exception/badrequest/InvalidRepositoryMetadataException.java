package org.example.githubfiles.exception.badrequest;

import org.example.githubfiles.exception.BadRequestException;

public class InvalidRepositoryMetadataException extends BadRequestException {
    public InvalidRepositoryMetadataException(String message) {
        super(message);
    }
}
