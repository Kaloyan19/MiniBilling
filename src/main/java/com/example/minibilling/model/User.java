package com.example.minibilling.model;

public record User(
        String name,
        String reference,
        int priceListNumber
) {
}
