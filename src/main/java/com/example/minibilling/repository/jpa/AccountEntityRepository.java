package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountEntityRepository extends JpaRepository<AccountEntity, String>{
    AccountEntity findByUsername(String username);
}
