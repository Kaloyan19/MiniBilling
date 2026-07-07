package com.example.minibilling.model;

import java.time.OffsetDateTime;
import java.util.List;

public record Invoice(
        OffsetDateTime documentDate,
        String documentNumber,
        String consumer,
        String reference,
        double totalAmount,
        List<InvoiceLine> lines
) {
}
