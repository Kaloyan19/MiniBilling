package com.example.minibilling.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String reference) {
        super("Потребител с референтен номер '" + reference + "' не е намерен");
    }
}
