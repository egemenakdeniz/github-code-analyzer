package org.example.githubfiles.exception;

public class InvalidRepositoryMetadataException extends RuntimeException  implements UserVisibleException{
    public InvalidRepositoryMetadataException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 400;
    }
}
