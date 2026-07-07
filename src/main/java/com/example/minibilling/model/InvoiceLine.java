package com.example.minibilling.model;

import java.time.OffsetDateTime;

public record InvoiceLine(
        int index,
        double quantity,
        OffsetDateTime lineStart,
        OffsetDateTime lineEnd,
        String product,
        double price,
        int priceList,
        double amount
) {
}
