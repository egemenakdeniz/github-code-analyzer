package org.example.githubfiles.exception;

public class RepositoryNotFoundException extends RuntimeException  implements UserVisibleException{
    public RepositoryNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
