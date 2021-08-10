package com.playtomic.tests.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorMessage {

    private LocalDateTime timestamp;
    private String message;
}
