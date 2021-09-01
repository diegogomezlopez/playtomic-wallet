package com.playtomic.tests.wallet.exception;

import org.springframework.dao.DataIntegrityViolationException;
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
        return getResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { WalletRepositoryException.class } )
    public ResponseEntity<ErrorMessage> handleWalletRepositoryException(WalletRepositoryException exception) {
        return getResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { WalletNotFoundException.class } )
    public ResponseEntity<ErrorMessage> handleWalletNotFoundException(WalletNotFoundException exception) {
        return getResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { WalletRechargeException.class } )
    public ResponseEntity<ErrorMessage> handleBalanceServiceException(WalletRechargeException exception) {
        return getResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { WalletChargeException.class } )
    public ResponseEntity<ErrorMessage> handleWalletChargeException(WalletChargeException exception) {
        return getResponse(exception, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class } )
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return getResponse(exception, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ErrorMessage> getResponse(final Exception exception, final HttpStatus status) {
        return new ResponseEntity<>(getErrorMessage(exception), new HttpHeaders(), status);
    }

    private String getLocalizedMessage(final Exception exception) {
        return Optional.ofNullable(exception.getLocalizedMessage())
                .orElse(exception.toString());
    }

    private ErrorMessage getErrorMessage(final Exception exception) {
        String errorMessageDescription = getLocalizedMessage(exception);
        return new ErrorMessage(LocalDateTime.now(), errorMessageDescription);
    }
}
