package com.example.minibilling.config;

import com.example.minibilling.model.entity.AccountEntity;
import com.example.minibilling.model.entity.Role;
import com.example.minibilling.repository.jpa.AccountEntityRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer {

    private final AccountEntityRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AccountEntityRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.user.username}")
    private String userUsername;

    @Value("${app.user.password}")
    private String userPassword;

    @PostConstruct
    public void init() {
        if (accountRepository.findByUsername(adminUsername) == null) {
            AccountEntity admin = new AccountEntity();
            admin.setId(UUID.randomUUID().toString().replace("-", ""));
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            accountRepository.save(admin);
        }

        if (accountRepository.findByUsername("user") == null) {
            AccountEntity user = new AccountEntity();
            user.setId(UUID.randomUUID().toString().replace("-", ""));
            user.setUsername(userUsername);
            user.setPassword(passwordEncoder.encode(userPassword));
            user.setRole(Role.USER);
            accountRepository.save(user);
        }
    }
}
