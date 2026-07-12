package com.redhat.deployforgeworker.exceptions;

import org.springframework.http.HttpStatus;

public class TempDirException extends DomainException {

    public TempDirException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_CONTENT, "Error creating temporary directory");
    }
}
