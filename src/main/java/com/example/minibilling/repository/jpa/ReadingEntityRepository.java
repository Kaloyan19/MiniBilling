package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.entity.ReadingEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReadingEntityRepository extends JpaRepository<ReadingEntity, String> {

    boolean existsByUserReferenceAndDateTime(String reference, OffsetDateTime dateTime);

    @NonNull
    @Query("SELECT r FROM ReadingEntity r JOIN FETCH r.user")
    List<ReadingEntity> findAll();

    @Query("SELECT r FROM ReadingEntity r WHERE r.user.reference = :reference")
    List<ReadingEntity> findByUserReference(@Param("reference") String reference);
}