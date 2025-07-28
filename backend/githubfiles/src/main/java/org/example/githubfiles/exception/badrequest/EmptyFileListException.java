package org.example.githubfiles.exception.badrequest;
import org.example.githubfiles.exception.BadRequestException;

public class EmptyFileListException extends BadRequestException {
    public EmptyFileListException(String message) {
        super(message);
    }
}
