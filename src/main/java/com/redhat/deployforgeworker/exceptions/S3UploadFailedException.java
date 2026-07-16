package com.redhat.deployforgeworker.exceptions;

import org.springframework.http.HttpStatus;

public class S3UploadFailedException extends DomainException{
    public S3UploadFailedException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "Upload to S3 failed");
    }
}
