package com.example.minibilling.repository;

import com.example.minibilling.model.domain.PricePeriod;
import com.example.minibilling.model.entity.PriceEntity;
import com.example.minibilling.repository.jpa.PriceEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceRepository {

    private final PriceEntityRepository priceEntityRepository;

    public PriceRepository(PriceEntityRepository priceEntityRepository){
        this.priceEntityRepository = priceEntityRepository;
    }

    public List<PricePeriod> findByPriceList(int priceList){
        return priceEntityRepository.findByPriceList(priceList)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    public List<PricePeriod> findAll() {
        return priceEntityRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private PricePeriod toDomain(PriceEntity entity){
        return new PricePeriod(
                entity.getProduct(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPrice(),
                entity.getPriceList()
        );
    }
}
