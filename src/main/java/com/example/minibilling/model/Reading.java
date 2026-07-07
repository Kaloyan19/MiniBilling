package com.example.minibilling.model;

import java.time.OffsetDateTime;

public record Reading(
        String customerReference,
        String product,
        OffsetDateTime date,
        double meterReading
) {
}
