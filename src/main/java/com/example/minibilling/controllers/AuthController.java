package com.example.minibilling.controllers;

import com.example.minibilling.model.domain.RegisterRequest;
import com.example.minibilling.model.entity.AccountEntity;
import com.example.minibilling.model.entity.Role;
import com.example.minibilling.repository.jpa.AccountEntityRepository;
import com.example.minibilling.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private final AccountEntityRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AccountEntityRepository accountRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody LoginRequest request) {
        AccountEntity account = accountRepository.findByUsername(request.username());
        if (account == null) {
            return ResponseEntity.status(401).body("Невалидно потребителско име или парола");
        }
        if (!passwordEncoder.matches(request.password(), account.getPassword())){
            return ResponseEntity.status(401).body("Невалидно потребителско име или парола");
        }
        String token = jwtService.generateToken(account.getUsername(), account.getRole().name());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (accountRepository.findByUsername(request.username()) != null) {
            return ResponseEntity.badRequest().body("Потребителското име вече съществува!");
        }
        AccountEntity account = new AccountEntity();
        account.setId(UUID.randomUUID().toString().replace("-", ""));
        account.setUsername(request.username());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setRole(Role.USER);
        account.setCustomerReference(request.customerReference());
        accountRepository.save(account);
        return ResponseEntity.ok("Регистрацията е успешна!");
    }
}
