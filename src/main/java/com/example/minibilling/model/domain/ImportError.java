package com.example.minibilling.model.domain;

public record ImportError(
        int line,
        String data,
        String error,
        boolean canFix
) {}
