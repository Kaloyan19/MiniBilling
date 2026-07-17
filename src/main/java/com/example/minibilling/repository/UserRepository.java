package com.example.minibilling.repository;

import com.example.minibilling.model.domain.User;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final UserEntityRepository userEntityRepository;

    public UserRepository(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    public List<User> findAll() {
        return userEntityRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    public User findByReference(String reference){
        var entity = userEntityRepository.findByReference(reference);
        if (entity == null) return null;
        return toDomain(entity);
    }

    private User toDomain(UserEntity entity) {
        return new User(entity.getName(), entity.getReference(), entity.getPriceList());
    }
}