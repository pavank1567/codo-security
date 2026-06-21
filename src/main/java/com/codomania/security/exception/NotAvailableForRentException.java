package com.codomania.security.exception;

public class NotAvailableForRentException extends RuntimeException{

    public NotAvailableForRentException(String message){
        super(message);
    }
}
