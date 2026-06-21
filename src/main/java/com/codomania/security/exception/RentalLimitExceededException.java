package com.codomania.security.exception;

public class RentalLimitExceededException extends RuntimeException {
    public RentalLimitExceededException(String message) {
        super(message);
    }
}
