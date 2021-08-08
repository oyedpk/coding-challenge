package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author oyedpk
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidTxnException extends RuntimeException {

    public InvalidTxnException(String message) {
        super(message);
    }
}
