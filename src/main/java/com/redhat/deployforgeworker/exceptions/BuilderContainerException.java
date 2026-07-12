package com.redhat.deployforgeworker.exceptions;

import org.springframework.http.HttpStatus;

public class BuilderContainerException extends DomainException{
    public BuilderContainerException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_CONTENT,"Could not build container");
    }
}
