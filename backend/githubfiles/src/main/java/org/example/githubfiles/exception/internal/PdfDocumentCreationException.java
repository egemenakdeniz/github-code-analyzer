package org.example.githubfiles.exception.internal;

import org.example.githubfiles.exception.InternalServerErrorException;

public class PdfDocumentCreationException extends InternalServerErrorException{
    public PdfDocumentCreationException(String message) {
        super(message);
    }
}
