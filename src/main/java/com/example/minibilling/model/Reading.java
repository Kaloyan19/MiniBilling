package com.example.minibilling.model;

import java.time.OffsetDateTime;

public record Reading(
        String customerReference,
        ProductType product,
        OffsetDateTime date,
        double meterReading
) {
}
