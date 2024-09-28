package com.grad.akemha.exception.authExceptions;

public class EmailAlreadyExistsException extends RegistrationException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}