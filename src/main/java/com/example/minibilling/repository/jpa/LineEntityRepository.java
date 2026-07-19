package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.entity.LineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineEntityRepository extends JpaRepository<LineEntity, String>{
}
