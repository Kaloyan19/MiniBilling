package com.example.minibilling.model.domain;

import java.time.LocalDate;

public record PricePeriod(
        ProductType product,
        LocalDate startDate,
        LocalDate endDate,
        double price,
        int priceListNumber
) {
    public PricePeriod {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "startDate не може да е след endDate: " + startDate + " > " + endDate);
        }
        if (price < 0) {
            throw new IllegalArgumentException("Цената не може да е отрицателна: " + price);
        }
        if (priceListNumber < 0) {
            throw new IllegalArgumentException("Невалиден номер на ценова листа: " + priceListNumber);
        }
    }
}
