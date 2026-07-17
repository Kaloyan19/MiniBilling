package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByReference(String reference);
}