package com.example.minibilling.model.domain;

import java.util.List;

public record VatLine(
        int index,
        List<Integer> lines,
        double percentage,
        double amount
) {}