package com.redhat.deployforgeworker.exceptions;

import org.springframework.http.HttpStatus;

public class DeploymentNotFoundException extends DomainException {

    public DeploymentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "DeploymentNotFound");
    }
}
