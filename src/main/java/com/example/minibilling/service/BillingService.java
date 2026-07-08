package com.example.minibilling.service;

import com.example.minibilling.model.*;
import com.example.minibilling.repository.PriceRepository;
import com.example.minibilling.repository.ReadingRepository;
import com.example.minibilling.repository.UserRepository;
import com.example.minibilling.validator.BillingDataValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BillingService {

    private final UserRepository userRepository;
    private final ReadingRepository readingRepository;
    private final PriceRepository priceRepository;
    private final BillingDataValidator validator;
    private final AtomicInteger invoiceCounter = new AtomicInteger(10000);

    public BillingService(UserRepository userRepository, ReadingRepository readingRepository, PriceRepository priceRepository, BillingDataValidator validator){
        this.userRepository = userRepository;
        this.readingRepository = readingRepository;
        this.priceRepository = priceRepository;
        this.validator = validator;
    }

    @PostConstruct
    public void validate() {
        validator.validateData(
                userRepository.findAll(),
                readingRepository.findAll(),
                priceRepository.findAll()
        );
    }

    public List<Invoice> generateAllInvoices(){
        return userRepository.findAll().stream()
                .map(this::generateInvoice)
                .toList();
    }

    private Invoice generateInvoice(User user) {
        List<Reading> readings = findReadings(user);
        List<PricePeriod> prices = findPrices(user);
        List<InvoiceLine> lines = createInvoiceLines(readings, prices, user);
        return buildInvoice(user, lines);
    }

    private List<Reading> findReadings(User user) {
        return readingRepository.findByCustomerReference(user.reference());
    }

    private List<PricePeriod> findPrices(User user) {
        return priceRepository.findByPriceList(user.priceListNumber());
    }

    private List<InvoiceLine> createInvoiceLines(List<Reading> readings,
                                                 List<PricePeriod> prices,
                                                 User user) {
        List<InvoiceLine> lines = new ArrayList<>();
        for (int i = 0; i < readings.size() - 1; i++) {
            lines.add(createInvoiceLine(readings.get(i),
                    readings.get(i + 1),
                    prices,
                    i + 1, user));
        }
        return lines;
    }

    private InvoiceLine createInvoiceLine(Reading from, Reading to,
                                          List<PricePeriod> prices,
                                          int index, User user) {
        double consumption = to.meterReading() - from.meterReading();
        List<PricePeriod> applicablePrices = findApplicablePricePeriods(prices, from, to);
        PricePeriod price = applicablePrices.get(0);
        double amount = consumption * price.price();

        return new InvoiceLine(index, consumption, from.date(), to.date(),
                from.product().name(), price.price(), user.priceListNumber(), amount);
    }

    private List<PricePeriod> findApplicablePricePeriods(
            List<PricePeriod> prices, Reading from, Reading to){
        LocalDate readingStart = from.date().toLocalDate();
        LocalDate readingEnd = to.date().toLocalDate();

        List<PricePeriod> applicable = prices.stream()
                .filter(p -> p.product() == from.product())
                .filter(p -> !p.startDate().isAfter(readingEnd))
                .filter(p -> !p.endDate().isBefore(readingStart))
                .toList();

        if (applicable.isEmpty()){
            throw new RuntimeException("Няма цена за периода: " + readingStart + " - " + readingEnd);
        }

        return applicable;
    }

    private Invoice buildInvoice(User user, List<InvoiceLine> lines) {
        double totalAmount = lines.stream()
                .mapToDouble(InvoiceLine::amount)
                .sum();
        return new Invoice(
                OffsetDateTime.now(),
                String.valueOf(invoiceCounter.getAndIncrement()),
                user.name(),
                user.reference(),
                totalAmount,
                lines
        );
    }

}
