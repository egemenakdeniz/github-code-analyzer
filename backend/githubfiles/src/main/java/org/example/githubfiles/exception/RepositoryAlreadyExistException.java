package org.example.githubfiles.exception;

public class RepositoryAlreadyExistException extends RuntimeException  implements UserVisibleException{
    public RepositoryAlreadyExistException(String message) {
        super(message);
    }
    @Override
    public int getStatusCode() {
        return 409;
    }
}
