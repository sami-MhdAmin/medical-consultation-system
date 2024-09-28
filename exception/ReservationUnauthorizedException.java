package com.grad.akemha.exception;

public class ReservationUnauthorizedException extends RuntimeException {
    public ReservationUnauthorizedException(String message) {
        super(message);
    }
}