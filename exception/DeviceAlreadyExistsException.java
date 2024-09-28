package com.grad.akemha.exception;

import com.grad.akemha.exception.authExceptions.RegistrationException;

public class DeviceAlreadyExistsException extends RuntimeException {
    public DeviceAlreadyExistsException(String message) {
        super(message);
    }
}
