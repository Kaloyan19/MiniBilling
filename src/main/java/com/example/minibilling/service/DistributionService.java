package com.example.minibilling.service;

import com.example.minibilling.model.domain.DistributionLine;
import com.example.minibilling.model.domain.PricePeriod;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DistributionService {

    public List<DistributionLine> distribute(OffsetDateTime start, OffsetDateTime end,
                                             double quantity, List<PricePeriod> prices) {
        List<PricePeriod> applicable = findApplicable(prices, start, end);
        applicable.forEach(p -> System.out.println(p.startDate() + " " + p.endDate() + " " + p.price()));

        if (applicable.size() == 1) {
            return singlePrice(start, end, quantity, applicable.get(0));
        }
        return multiplePrices(start, end, quantity, applicable);
    }

    private List<DistributionLine> singlePrice(OffsetDateTime start, OffsetDateTime end,
                                               double quantity, PricePeriod price) {
        OffsetDateTime lineEnd = subPeriodEnd(price, end);
        return List.of(new DistributionLine(start, lineEnd, quantity, price.price()));
    }

    private List<DistributionLine> multiplePrices(OffsetDateTime start, OffsetDateTime end,
                                                  double quantity, List<PricePeriod> applicable) {
        List<DistributionLine> lines = new ArrayList<>();
        int totalDays = countDays(start, end);
        OffsetDateTime currentStart = start;
        double distributed = 0;

        for (int i = 0; i < applicable.size() - 1; i++) {
            PricePeriod price = applicable.get(i);
            OffsetDateTime lineEnd = subPeriodEnd(price, end);
            int subDays = countDays(currentStart, lineEnd);
            double ratio = round2((double) subDays / totalDays);
            double qty = round2(quantity * ratio);
            distributed += qty;

            lines.add(new DistributionLine(currentStart, lineEnd, qty, price.price()));
            currentStart = nextPeriodStart(lineEnd);
        }

        PricePeriod lastPrice = applicable.get(applicable.size() - 1);
        double lastQty = round2(quantity - distributed);
        lines.add(new DistributionLine(currentStart, end, lastQty, lastPrice.price()));

        return lines;
    }

    private List<PricePeriod> findApplicable(List<PricePeriod> prices,
                                             OffsetDateTime start, OffsetDateTime end) {
        return prices.stream()
                .filter(p -> !p.startDate().isAfter(end.toLocalDate()))
                .filter(p -> !p.endDate().isBefore(start.toLocalDate()))
                .sorted(Comparator.comparing(PricePeriod::startDate)) // ← сортираме
                .toList();
    }

    private OffsetDateTime subPeriodEnd(PricePeriod price, OffsetDateTime qEnd) {
        OffsetDateTime priceEnd = price.endDate()
                .atTime(23, 59, 59)
                .atOffset(ZoneOffset.UTC);
        return priceEnd.isBefore(qEnd) ? priceEnd : qEnd;
    }

    private OffsetDateTime nextPeriodStart(OffsetDateTime lineEnd) {
        return lineEnd.plusSeconds(1)
                .withOffsetSameInstant(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.DAYS);
    }

    private int countDays(OffsetDateTime start, OffsetDateTime end) {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private double round2(double value) {
        return Math.ceil(value * 100) / 100.0;
    }
}