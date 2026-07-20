package com.example.minibilling.model.domain;

import java.time.OffsetDateTime;

public record DistributionLine(
        OffsetDateTime start,
        OffsetDateTime end,
        double quantity,
        double price
) {}