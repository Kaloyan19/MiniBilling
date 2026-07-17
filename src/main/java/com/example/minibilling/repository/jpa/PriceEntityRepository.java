package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriceEntityRepository extends JpaRepository<PriceEntity, String>{
    List<PriceEntity> findByPriceList(int priceList);
}
