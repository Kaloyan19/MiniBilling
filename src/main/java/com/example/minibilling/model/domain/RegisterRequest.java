package com.example.minibilling.model.domain;

public record RegisterRequest(
        String username,
        String password,
        String customerReference
) {
}
