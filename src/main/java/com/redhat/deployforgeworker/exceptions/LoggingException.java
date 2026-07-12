package com.redhat.deployforgeworker.exceptions;

import org.springframework.http.HttpStatus;

public class LoggingException extends DomainException{
    public LoggingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "Error Fetching Logs from builder");
    }
}
