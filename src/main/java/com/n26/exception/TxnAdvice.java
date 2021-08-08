package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class TxnAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleException(HttpMessageNotReadableException exception
            , HttpServletRequest request) {
        return new ResponseEntity("Request invalid", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTxnException.class)
    public ResponseEntity<String> handleException(InvalidTxnException exception
            , HttpServletRequest request) {
        return new ResponseEntity("Request invalid", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(OldTxnException.class)
    public ResponseEntity<String> handleException(OldTxnException exception
            , HttpServletRequest request) {
        return new ResponseEntity("Txn Date is outdated", HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception
            , HttpServletRequest request) {
        return new ResponseEntity("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}