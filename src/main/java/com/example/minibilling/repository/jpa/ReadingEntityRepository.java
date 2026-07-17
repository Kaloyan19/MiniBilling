package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.entity.ReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReadingEntityRepository extends JpaRepository<ReadingEntity, String>{
    List<ReadingEntity> findByUserReference(String reference);
}
