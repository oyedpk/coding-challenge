package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author oyedpk
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class OldTxnException extends RuntimeException {

    public OldTxnException(String message) {
        super(message);
    }
}
