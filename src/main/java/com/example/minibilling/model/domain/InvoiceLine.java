package com.example.minibilling.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record InvoiceLine(
        int index,
        BigDecimal quantity,
        OffsetDateTime lineStart,
        OffsetDateTime lineEnd,
        String product,
        String unit,
        BigDecimal price,
        int priceList,
        BigDecimal amount,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String name,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<Integer> lines
) {}