package com.example.minibilling.validator;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.repository.jpa.PriceEntityRepository;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ImportValidator {
    private final UserEntityRepository userEntityRepository;
    private final PriceEntityRepository priceEntityRepository;

    public ImportValidator(UserEntityRepository userEntityRepository, PriceEntityRepository priceEntityRepository) {
        this.userEntityRepository = userEntityRepository;
        this.priceEntityRepository = priceEntityRepository;
    }

    public void validateUserPriceList(int priceList) throws ImportException {
        if (priceEntityRepository.findByPriceList(priceList).isEmpty()){
            throw new ImportException(
                    "Ценова листа " + priceList + " не съществува в базата данни!");
        }
    }

    public void validateReadingUser(String reference) throws ImportException {
        if (userEntityRepository.findByReference(reference) == null){
            throw new ImportException(
                    "Потребител с референтен номер " + reference + " не съществува!");
        }
    }

    public void validateUser(String name, String reference, int priceList) throws ImportException {
        if (name == null || name.isBlank()) {
            throw new ImportException("Името не може да е празно");
        }
        if (reference == null || reference.isBlank()) {
            throw new ImportException("Референтният номер не може да е празен");
        }
        if (priceList < 1) {
            throw new ImportException("Невалиден номер на ценова листа: " + priceList);
        }
    }

    public void validateReading(String reference, BigDecimal lastReading) throws ImportException {
        if (reference == null || reference.isBlank()) {
            throw new ImportException("Референтният номер не може да е празен");
        }
        if (lastReading.compareTo(BigDecimal.ZERO) < 0) {
            throw new ImportException("Показанието не може да е отрицателно: " + lastReading);
        }
    }

    public void validatePrice(LocalDate startDate, LocalDate endDate, double price) throws ImportException {
        if (startDate.isAfter(endDate)) {
            throw new ImportException("startDate не може да е след endDate: " + startDate + " > " + endDate);
        }
        if (price <= 0) {
            throw new ImportException("Цената трябва да е положително число: " + price);
        }
    }
}
