package com.example.minibilling.model;

public record User(
        String name,
        String reference,
        int priceListNumber
) {
    public User {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Името не може да е празно");
        }
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Референтният номер не може да е празен");
        }
        if (priceListNumber < 1) {
            throw new IllegalArgumentException("Невалиден номер на ценова листа: " + priceListNumber);
        }
    }
}