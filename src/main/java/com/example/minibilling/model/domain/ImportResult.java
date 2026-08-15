package com.example.minibilling.model.domain;

import java.util.List;

public record ImportResult(
        int success,
        int failed,
        List<ImportError> errors
) {}
