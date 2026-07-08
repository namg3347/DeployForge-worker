package com.redhat.deployforgeworker.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class DomainException extends RuntimeException {
    private final HttpStatus status;
    private final String statusCode;

    public DomainException(String message, HttpStatus status, String statusCode) {
        super(message);
        this.status = status;
        this.statusCode = statusCode;
    }
}
