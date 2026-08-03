package com.example.minibilling.model.domain;

import java.time.OffsetDateTime;
import java.util.List;

public record InvoiceLine(
        int index,
        double quantity,
        OffsetDateTime lineStart,
        OffsetDateTime lineEnd,
        String product,
        String unit,
        double price,
        int priceList,
        double amount,
        String name,
        List<Integer> lines
) {}
