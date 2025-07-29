package org.example.githubfiles.exception.conflict;

import org.example.githubfiles.exception.ConflictException;

public class AlreadyBeingAnalyzedException extends ConflictException {
    public AlreadyBeingAnalyzedException(String message) {
        super(message);
    }
}
