package com.playtomic.tests.wallet.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Optional;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = { Exception.class } )
    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
        String errorMessageDescription = getLocalizedMessage(exception);
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { WalletRepositoryException.class } )
    public ResponseEntity<ErrorMessage> handleWalletRepositoryException(WalletRepositoryException exception) {
        String errorMessageDescription = getLocalizedMessage(exception);
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { WalletNotFoundException.class } )
    public ResponseEntity<ErrorMessage> handleWalletNotFoundException(WalletNotFoundException exception) {
        String errorMessageDescription = getLocalizedMessage(exception);
        ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), errorMessageDescription);
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    private String getLocalizedMessage(final Exception exception) {
        return Optional.ofNullable(exception.getLocalizedMessage())
                .orElse(exception.toString());
    }
}