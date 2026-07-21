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

    @Test
    void sc2_twoMeasurementsOverlappingDay() {
        List<PricePeriod> prices = List.of(
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,25), LocalDate.of(2023,11,6), 0.30, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,11,7), LocalDate.of(2023,11,18), 0.35, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,11,19), LocalDate.of(2023,12,4), 0.32, 1)
        );

        //Q1

        List<DistributionLine> result1 = service.distribute(
                OffsetDateTime.parse("2023-11-01T13:23:00+02:00"),
                OffsetDateTime.parse("2023-11-06T15:20:00+02:00"),
                20.00, prices);

        assertEquals(1, result1.size());
        assertEquals(OffsetDateTime.parse("2023-11-01T13:23:00+02:00"), result1.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-11-06T15:20:00+02:00"), result1.get(0).end());
        assertEquals(20.00, result1.get(0).quantity());
        assertEquals(0.30, result1.get(0).price());

        //Q2

        List<DistributionLine> result2 = service.distribute(
                OffsetDateTime.parse("2023-11-06T15:20:01+02:00"),
                OffsetDateTime.parse("2023-11-30T20:20:00+02:00"),
                100.00, prices);

        assertEquals(3, result2.size());
        assertEquals(OffsetDateTime.parse("2023-11-06T15:20:01+02:00"), result2.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-11-06T23:59:59+02:00"), result2.get(0).end());
        assertEquals(4.00, result2.get(0).quantity());
        assertEquals(0.30, result2.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-11-07T00:00:00+02:00"), result2.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-11-18T23:59:59+02:00"), result2.get(1).end());
        assertEquals(48.00, result2.get(1).quantity());
        assertEquals(0.35, result2.get(1).price());

        assertEquals(OffsetDateTime.parse("2023-11-19T00:00:00+02:00"), result2.get(2).start());
        assertEquals(OffsetDateTime.parse("2023-11-30T20:20:00+02:00"), result2.get(2).end());
        assertEquals(48.00, result2.get(2).quantity());
        assertEquals(0.32, result2.get(2).price());
    }

    @Test
    void sc3_threeMeasurementsTimezoneChange() {
        List<PricePeriod> prices = List.of(
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,1), LocalDate.of(2023,10,6), 0.28, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,7), LocalDate.of(2023,10,17), 0.31, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,18), LocalDate.of(2023,10,30), 0.37, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,31), LocalDate.of(2023,11,7), 0.35, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,11,8), LocalDate.of(2023,11,14), 0.32, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,11,15), LocalDate.of(2023,11,30), 0.42, 1)
        );

        // Q1: 01.10 → 19.10, quantity=19.23
        List<DistributionLine> r1 = service.distribute(
                OffsetDateTime.parse("2023-10-01T11:43:12+03:00"),
                OffsetDateTime.parse("2023-10-19T10:09:59+03:00"),
                19.23, prices);

        assertEquals(3, r1.size());
        assertEquals(OffsetDateTime.parse("2023-10-01T11:43:12+03:00"), r1.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-10-06T23:59:59+03:00"), r1.get(0).end());
        assertEquals(6.16, r1.get(0).quantity());
        assertEquals(0.28, r1.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-10-07T00:00:00+03:00"), r1.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-10-17T23:59:59+03:00"), r1.get(1).end());
        assertEquals(11.16, r1.get(1).quantity());
        assertEquals(0.31, r1.get(1).price());

        assertEquals(OffsetDateTime.parse("2023-10-18T00:00:00+03:00"), r1.get(2).start());
        assertEquals(OffsetDateTime.parse("2023-10-19T10:09:59+03:00"), r1.get(2).end());
        assertEquals(1.91, r1.get(2).quantity());
        assertEquals(0.37, r1.get(2).price());

        // Q2: 19.10 → 01.11, quantity=37.81
        List<DistributionLine> r2 = service.distribute(
                OffsetDateTime.parse("2023-10-19T10:10:00+03:00"),
                OffsetDateTime.parse("2023-11-01T14:01:23+02:00"),
                37.81, prices);

        assertEquals(2, r2.size());
        assertEquals(OffsetDateTime.parse("2023-10-19T10:10:00+03:00"), r2.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-10-30T23:59:59+02:00"), r2.get(0).end());
        assertEquals(32.52, r2.get(0).quantity());
        assertEquals(0.37, r2.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-10-31T00:00:00+02:00"), r2.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-11-01T14:01:23+02:00"), r2.get(1).end());
        assertEquals(5.29, r2.get(1).quantity());
        assertEquals(0.35, r2.get(1).price());

        // Q3: 01.11 → 14.11, quantity=42.42
        List<DistributionLine> r3 = service.distribute(
                OffsetDateTime.parse("2023-11-01T14:01:24+02:00"),
                OffsetDateTime.parse("2023-11-14T08:23:14+02:00"),
                42.42, prices);

        assertEquals(2, r3.size());
        assertEquals(OffsetDateTime.parse("2023-11-01T14:01:24+02:00"), r3.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-11-07T23:59:59+02:00"), r3.get(0).end());
        assertEquals(21.21, r3.get(0).quantity());
        assertEquals(0.35, r3.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-11-08T00:00:00+02:00"), r3.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-11-14T08:23:14+02:00"), r3.get(1).end());
        assertEquals(21.21, r3.get(1).quantity());
        assertEquals(0.32, r3.get(1).price());
    }

    @Test
    void sc4_singleLongMeasurementManyPrices() {
        List<PricePeriod> prices = List.of(
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,1), LocalDate.of(2023,7,1), 0.28, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,2), LocalDate.of(2023,7,3), 0.29, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,4), LocalDate.of(2023,7,6), 0.30, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,7), LocalDate.of(2023,7,10), 0.31, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,11), LocalDate.of(2023,7,15), 0.32, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,16), LocalDate.of(2023,7,21), 0.33, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,22), LocalDate.of(2023,7,28), 0.34, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,7,29), LocalDate.of(2023,8,13), 0.35, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,8,14), LocalDate.of(2023,8,29), 0.34, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,8,30), LocalDate.of(2023,9,27), 0.33, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,9,28), LocalDate.of(2023,10,29), 0.32, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,30), LocalDate.of(2023,10,30), 0.31, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,10,31), LocalDate.of(2023,10,31), 0.30, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023,11,1), LocalDate.of(2023,11,19), 0.01, 1)
        );

        List<DistributionLine> result = service.distribute(
                OffsetDateTime.parse("2023-07-01T11:43:12+03:00"),
                OffsetDateTime.parse("2023-11-19T10:09:59+02:00"),
                940930.29, prices);

        assertEquals(14, result.size());

        assertEquals(9409.31, result.get(0).quantity());
        assertEquals(0.28, result.get(0).price());
        assertEquals(OffsetDateTime.parse("2023-07-01T23:59:59+03:00"), result.get(0).end());

        assertEquals(18818.61, result.get(1).quantity());
        assertEquals(0.29, result.get(1).price());

        assertEquals(28227.91, result.get(2).quantity());
        assertEquals(0.30, result.get(2).price());

        assertEquals(28227.91, result.get(3).quantity());
        assertEquals(0.31, result.get(3).price());

        assertEquals(37637.22, result.get(4).quantity());
        assertEquals(0.32, result.get(4).price());

        assertEquals(47046.52, result.get(5).quantity());
        assertEquals(0.33, result.get(5).price());

        assertEquals(47046.52, result.get(6).quantity());
        assertEquals(0.34, result.get(6).price());

        assertEquals(112911.64, result.get(7).quantity());
        assertEquals(0.35, result.get(7).price());

        assertEquals(112911.64, result.get(8).quantity());
        assertEquals(0.34, result.get(8).price());

        assertEquals(197595.37, result.get(9).quantity());
        assertEquals(0.33, result.get(9).price());

        assertEquals(216413.97, result.get(10).quantity());
        assertEquals(0.32, result.get(10).price());

        assertEquals(9409.31, result.get(11).quantity());
        assertEquals(0.31, result.get(11).price());
        assertEquals(OffsetDateTime.parse("2023-10-30T23:59:59+02:00"), result.get(11).end());

        assertEquals(9409.31, result.get(12).quantity());
        assertEquals(0.30, result.get(12).price());

        assertEquals(65865.05, result.get(13).quantity());
        assertEquals(0.01, result.get(13).price());
        assertEquals(OffsetDateTime.parse("2023-11-19T10:09:59+02:00"), result.get(13).end());
    }

    @Test
    void sc5_fourMeasurementsWithTimezoneChange() {
        List<PricePeriod> prices = List.of(
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 1), 0.28, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 2), LocalDate.of(2023, 7, 3), 0.29, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 4), LocalDate.of(2023, 7, 6), 0.30, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 7), LocalDate.of(2023, 7, 10), 0.31, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 11), LocalDate.of(2023, 7, 15), 0.32, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 16), LocalDate.of(2023, 7, 21), 0.33, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 22), LocalDate.of(2023, 7, 28), 0.34, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 7, 29), LocalDate.of(2023, 8, 13), 0.35, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 8, 14), LocalDate.of(2023, 8, 29), 0.34, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 8, 30), LocalDate.of(2023, 9, 27), 0.33, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 9, 28), LocalDate.of(2023, 10, 29), 0.32, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 10, 30), LocalDate.of(2023, 10, 30), 0.31, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 10, 31), LocalDate.of(2023, 10, 31), 0.30, 1),
                new PricePeriod(ProductType.GAS, LocalDate.of(2023, 11, 1), LocalDate.of(2023, 11, 19), 0.01, 1)
        );

        //Q1 - 01.07 - 19.07, quantity = 940930.29
        List<DistributionLine> result1 = service.distribute(
                OffsetDateTime.parse("2023-07-01T11:43:12+03:00"),
                OffsetDateTime.parse("2023-07-19T10:09:59+03:00"),
                940930.29, prices);

        assertEquals(6, result1.size());

        assertEquals(OffsetDateTime.parse("2023-07-01T11:43:12+03:00"), result1.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-07-01T23:59:59+03:00"), result1.get(0).end());
        assertEquals(56455.82, result1.get(0).quantity());
        assertEquals(0.28, result1.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-07-02T00:00:00+03:00"), result1.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-07-03T23:59:59+03:00"), result1.get(1).end());
        assertEquals(103502.34, result1.get(1).quantity());
        assertEquals(0.29, result1.get(1).price());

        assertEquals(OffsetDateTime.parse("2023-07-04T00:00:00+03:00"), result1.get(2).start());
        assertEquals(OffsetDateTime.parse("2023-07-06T23:59:59+03:00"), result1.get(2).end());
        assertEquals(150548.85, result1.get(2).quantity());
        assertEquals(0.30, result1.get(2).price());

        assertEquals(OffsetDateTime.parse("2023-07-07T00:00:00+03:00"), result1.get(3).start());
        assertEquals(OffsetDateTime.parse("2023-07-10T23:59:59+03:00"), result1.get(3).end());
        assertEquals(207004.67, result1.get(3).quantity());
        assertEquals(0.31, result1.get(3).price());

        assertEquals(OffsetDateTime.parse("2023-07-11T00:00:00+03:00"), result1.get(4).start());
        assertEquals(OffsetDateTime.parse("2023-07-15T23:59:59+03:00"), result1.get(4).end());
        assertEquals(254051.18, result1.get(4).quantity());
        assertEquals(0.32, result1.get(4).price());

        assertEquals(OffsetDateTime.parse("2023-07-16T00:00:00+03:00"), result1.get(5).start());
        assertEquals(OffsetDateTime.parse("2023-07-19T10:09:59+03:00"), result1.get(5).end());
        assertEquals(169367.43, result1.get(5).quantity());
        assertEquals(0.33, result1.get(5).price());

        //Q2 - 19.07 - 11.09 - quantity: 3397.91

        List<DistributionLine> result2 = service.distribute(
                OffsetDateTime.parse("2023-07-19T10:10:00+03:00"),
                OffsetDateTime.parse("2023-09-11T15:09:59+03:00"),
                3397.91, prices);

        assertEquals(5, result2.size());

        assertEquals(OffsetDateTime.parse("2023-07-19T10:10:00+03:00"), result2.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-07-21T23:59:59+03:00"), result2.get(0).end());
        assertEquals(203.88, result2.get(0).quantity());
        assertEquals(0.33, result2.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-07-22T00:00:00+03:00"), result2.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-07-28T23:59:59+03:00"), result2.get(1).end());
        assertEquals(441.73, result2.get(1).quantity());
        assertEquals(0.34, result2.get(1).price());

        assertEquals(OffsetDateTime.parse("2023-07-29T00:00:00+03:00"), result2.get(2).start());
        assertEquals(OffsetDateTime.parse("2023-08-13T23:59:59+03:00"), result2.get(2).end());
        assertEquals(1019.38, result2.get(2).quantity());
        assertEquals(0.35, result2.get(2).price());

        assertEquals(OffsetDateTime.parse("2023-08-14T00:00:00+03:00"), result2.get(3).start());
        assertEquals(OffsetDateTime.parse("2023-08-29T23:59:59+03:00"), result2.get(3).end());
        assertEquals(1019.38, result2.get(3).quantity());
        assertEquals(0.34, result2.get(3).price());

        assertEquals(OffsetDateTime.parse("2023-08-30T00:00:00+03:00"), result2.get(4).start());
        assertEquals(OffsetDateTime.parse("2023-09-11T15:09:59+03:00"), result2.get(4).end());
        assertEquals(713.54, result2.get(4).quantity());
        assertEquals(0.33, result2.get(4).price());

        //Q3 - 11.09 - 19.10 - quantity: 2342.73

        List<DistributionLine> result3 = service.distribute(
                OffsetDateTime.parse("2023-09-11T15:10:00+03:00"),
                OffsetDateTime.parse("2023-10-19T13:14:00+03:00"),
                2342.73, prices);

        assertEquals(2, result3.size());

        assertEquals(OffsetDateTime.parse("2023-09-11T15:10:00+03:00"), result3.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-09-27T23:59:59+03:00"), result3.get(0).end());
        assertEquals(1030.81, result3.get(0).quantity());
        assertEquals(0.33, result3.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-09-28T00:00:00+03:00"), result3.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-10-19T13:14:00+03:00"), result3.get(1).end());
        assertEquals(1311.92, result3.get(1).quantity());
        assertEquals(0.32, result3.get(1).price());

        //Q4 - 19.10 - 19.11, quantity:932472.16

        List<DistributionLine> result4 = service.distribute(
                OffsetDateTime.parse("2023-10-19T13:14:01+03:00"),
                OffsetDateTime.parse("2023-11-19T10:09:59+02:00"),
                932472.16, prices);

        assertEquals(4, result4.size());

        assertEquals(OffsetDateTime.parse("2023-10-19T13:14:01+03:00"), result4.get(0).start());
        assertEquals(OffsetDateTime.parse("2023-10-29T23:59:59+02:00"), result4.get(0).end());
        assertEquals(326365.26, result4.get(0).quantity());
        assertEquals(0.32, result4.get(0).price());

        assertEquals(OffsetDateTime.parse("2023-10-30T00:00:00+02:00"), result4.get(1).start());
        assertEquals(OffsetDateTime.parse("2023-10-30T23:59:59+02:00"), result4.get(1).end());
        assertEquals(37298.89, result4.get(1).quantity());
        assertEquals(0.31, result4.get(1).price());

        assertEquals(OffsetDateTime.parse("2023-10-31T00:00:00+02:00"), result4.get(2).start());
        assertEquals(OffsetDateTime.parse("2023-10-31T23:59:59+02:00"), result4.get(2).end());
        assertEquals(37298.89, result4.get(2).quantity());
        assertEquals(0.30, result4.get(2).price());

        assertEquals(OffsetDateTime.parse("2023-11-01T00:00:00+02:00"), result4.get(3).start());
        assertEquals(OffsetDateTime.parse("2023-11-19T10:09:59+02:00"), result4.get(3).end());
        assertEquals(531509.12, result4.get(3).quantity());
        assertEquals(0.01, result4.get(3).price());
    }

}
