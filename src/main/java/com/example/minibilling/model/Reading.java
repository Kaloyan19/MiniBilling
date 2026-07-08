package com.example.minibilling.model;

import java.time.OffsetDateTime;

public record Reading(
        String customerReference,
        ProductType product,
        OffsetDateTime date,
        double meterReading
) {
    public Reading {
        if (customerReference == null || customerReference.isBlank()) {
            throw new IllegalArgumentException("Референтният номер не може да е празен");
        }
        if (date == null) {
            throw new IllegalArgumentException("Датата не може да е null");
        }
        if (meterReading < 0) {
            throw new IllegalArgumentException("Показанието не може да е отрицателно: " + meterReading);
        }
    }
}