package com.example.minibilling.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record Invoice(
        OffsetDateTime documentDate,
        String documentNumber,
        String consumer,
        String reference,
        BigDecimal totalAmount,
        BigDecimal totalAmountWithVat,
        List<InvoiceLine> lines,
        List<VatLine> vat
) {}