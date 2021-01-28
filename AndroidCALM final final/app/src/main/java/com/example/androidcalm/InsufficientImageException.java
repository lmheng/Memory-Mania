package com.example.androidcalm;

public class InsufficientImageException extends Exception {
    public InsufficientImageException() {
    }

    public InsufficientImageException(String message) {
        super(message);
    }
}
