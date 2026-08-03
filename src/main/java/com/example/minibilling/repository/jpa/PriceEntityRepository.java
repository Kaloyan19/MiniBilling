package com.example.minibilling.repository.jpa;

import com.example.minibilling.model.domain.ProductType;
import com.example.minibilling.model.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceEntityRepository extends JpaRepository<PriceEntity, String>{
    boolean existsByProductAndStartDateAndPriceList(ProductType product, LocalDate startDate, int priceList);
    List<PriceEntity> findByPriceList(int priceList);

    List<PriceEntity> findByPriceListAndProduct(int priceList, ProductType product);
}
