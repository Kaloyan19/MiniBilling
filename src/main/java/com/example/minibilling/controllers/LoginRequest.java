package com.example.minibilling.controllers;

public record LoginRequest(
        String username,
        String password
) {
}
