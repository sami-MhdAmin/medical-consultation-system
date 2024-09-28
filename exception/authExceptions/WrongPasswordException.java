package com.grad.akemha.exception.authExceptions;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message) {
        super(message);
    }

}
