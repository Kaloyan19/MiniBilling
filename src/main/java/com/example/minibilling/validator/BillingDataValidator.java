package com.example.minibilling.validator;

import com.example.minibilling.exception.BillingDataException;
import com.example.minibilling.model.domain.PricePeriod;
import com.example.minibilling.model.domain.Reading;
import com.example.minibilling.model.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BillingDataValidator {

    public void validateData(List<User> users, List<Reading> readings, List<PricePeriod> prices) {
        validatePriceLists(users, prices);
        validateCustomerReferences(users, readings);
    }

    private void validatePriceLists(List<User> users, List<PricePeriod> prices) {
        Set<Integer> availablePriceLists = prices.stream()
                .map(PricePeriod::priceListNumber)
                .collect(Collectors.toSet());

        users.forEach(u -> {
            if (!availablePriceLists.contains(u.priceListNumber())) {
                throw new BillingDataException("Потребител " + u.reference() +
                        " сочи към несъществуваща ценова листа: " + u.priceListNumber());
            }
        });
    }

    private void validateCustomerReferences(List<User> users, List<Reading> readings) {
        Set<String> userReferences = users.stream()
                .map(User::reference)
                .collect(Collectors.toSet());

        readings.forEach(r -> {
            if (!userReferences.contains(r.customerReference())) {
                throw new BillingDataException("Reading за несъществуващ потребител: "
                        + r.customerReference());
            }
        });
    }
}