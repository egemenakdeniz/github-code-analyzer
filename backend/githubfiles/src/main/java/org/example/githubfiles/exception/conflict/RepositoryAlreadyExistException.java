package org.example.githubfiles.exception.conflict;
import org.example.githubfiles.exception.ConflictException;

public class RepositoryAlreadyExistException extends ConflictException {
    public RepositoryAlreadyExistException(String message) {
        super(message);
    }
}
