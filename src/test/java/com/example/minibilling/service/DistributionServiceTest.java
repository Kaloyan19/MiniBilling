package com.example.minibilling.service;

import com.example.minibilling.model.domain.DistributionLine;
import com.example.minibilling.model.domain.PricePeriod;
import com.example.minibilling.model.domain.ProductType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DistributionServiceTest {

    private final DistributionService service = new DistributionService();

    @Test
    void sc1_singleMeasurementMultiplePrices() {
        OffsetDateTime start = OffsetDateTime.parse("2023-11-01T13:23:00+02:00");
        OffsetDateTime end = OffsetDateTime.parse("2023-11-30T15:20:00+02:00");
        double quantity = 120.00;

        List<PricePeriod> prices = List.of(
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 10, 25), LocalDate.of(2023, 11, 6), 0.30, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 11, 7), LocalDate.of(2023, 11, 18), 0.35, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 11, 19), LocalDate.of(2023, 12, 4), 0.32, 1)
        );

        List<DistributionLine> result = service.distribute(start, end, quantity, prices);

        assertEquals(3, result.size());

        assertEquals(OffsetDateTime.parse("2023-11-01T13:23:00+02:00"), result.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-11-06T23:59:59+02:00"), result.get(0).end());
        assertEquals(24.00, result.get(0).quantity());
        assertEquals(0.30, result.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-11-07T00:00:00+02:00"), result.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-11-18T23:59:59+02:00"), result.get(1).end());
        assertEquals(48.00, result.get(1).quantity());
        assertEquals(0.35, result.get(1).price());

        assertEquals(OffsetDateTime.parse("2023-11-19T00:00:00+02:00"), result.get(2).start());
        assertEquals(OffsetDateTime.parse("2023-11-30T15:20:00+02:00"), result.get(2).end());
        assertEquals(48.00, result.get(2).quantity());
        assertEquals(0.32, result.get(2).price());
    }
}
