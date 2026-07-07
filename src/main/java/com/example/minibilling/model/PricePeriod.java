package com.example.minibilling.model;

import java.time.LocalDate;

public record PricePeriod(
        String product,
        LocalDate startDate,
        LocalDate endDate,
        double price,
        int priceListNumber
) {
}
