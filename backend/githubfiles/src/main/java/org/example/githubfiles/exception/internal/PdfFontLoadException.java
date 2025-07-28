package org.example.githubfiles.exception.internal;

import org.example.githubfiles.exception.InternalServerErrorException;

public class PdfFontLoadException extends InternalServerErrorException {
    public PdfFontLoadException(String message) {
        super(message);
    }
}
