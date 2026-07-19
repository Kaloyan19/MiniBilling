package com.example.minibilling.service;

import com.example.minibilling.exception.UserNotFoundException;
import com.example.minibilling.model.domain.*;
import com.example.minibilling.model.entity.InvoiceEntity;
import com.example.minibilling.model.entity.LineEntity;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.InvoiceRepository;
import com.example.minibilling.repository.PriceRepository;
import com.example.minibilling.repository.ReadingRepository;
import com.example.minibilling.repository.UserRepository;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BillingService {

    private final UserRepository userRepository;
    private final ReadingRepository readingRepository;
    private final PriceRepository priceRepository;
    private final AtomicInteger invoiceCounter = new AtomicInteger(10000);
    private final InvoiceRepository invoiceRepository;
    private final UserEntityRepository userEntityRepository;

    public BillingService(UserRepository userRepository, ReadingRepository readingRepository, PriceRepository priceRepository,
                          InvoiceRepository invoiceRepository, UserEntityRepository userEntityRepository){
        this.userRepository = userRepository;
        this.readingRepository = readingRepository;
        this.priceRepository = priceRepository;
        this.invoiceRepository = invoiceRepository;
        this.userEntityRepository = userEntityRepository;
    }

    public Optional<Invoice> generateAndSaveInvoice(String reference, YearMonth period) {
        Optional<Invoice> invoice = generateInvoice(reference, period);
        if (invoice.isEmpty()) return Optional.empty();

        saveToDatabase(invoice.get(), reference, period);
        return invoice;
    }

    public Optional<Invoice> generateInvoice(String reference, YearMonth period){
        User user = userRepository.findAll().stream()
                .filter(u-> u.reference().equals(reference))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(reference));

        List<Reading> readings = findReadings(user, period);
        if (readings.size() < 2){
            return Optional.empty();
        }

        List<PricePeriod> prices = findPrices(user);
        List<InvoiceLine> lines = createInvoiceLines(readings, prices, user);
        return Optional.of(buildInvoice(user, lines));
    }

    private List<Reading> findReadings(User user, YearMonth period) {
        LocalDate endOfPeriod = period.atEndOfMonth();

        return readingRepository.findByCustomerReference(user.reference())
                .stream()
                .filter(r -> !r.date().toLocalDate().isAfter(endOfPeriod))
                .sorted(Comparator.comparing(Reading::date))
                .toList();
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

        double consumption = round2(to.meterReading() - from.meterReading());
        List<PricePeriod> applicablePrices = findApplicablePricePeriods(prices, from, to);
        PricePeriod price = applicablePrices.get(0);
        double amount = round2(consumption * price.price());

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
        double totalAmount = round2(lines.stream().
                mapToDouble(InvoiceLine::amount)
                .sum());
        return new Invoice(
                OffsetDateTime.now(),
                String.valueOf(invoiceCounter.getAndIncrement()),
                user.name(),
                user.reference(),
                totalAmount,
                lines
        );
    }

    public String findConsumerName(String reference) {
        return userRepository.findAll().stream()
                .filter(u -> u.reference().equals(reference))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(reference))
                .name();
    }

    private double round2(double value){
        return Math.ceil(value * 100) / 100.00;
    }

    private double round3(double value){
        return Math.ceil(value * 1000) / 100.00;
    }

    private void saveToDatabase(Invoice invoice, String reference, YearMonth period) {
        UserEntity userEntity = userEntityRepository.findByReference(reference);

        InvoiceEntity entity = new InvoiceEntity();
        entity.setId(UUID.randomUUID().toString().replace("-", ""));
        entity.setDateTime(invoice.documentDate());
        entity.setNumber(invoice.documentNumber());
        entity.setUser(userEntity);
        entity.setTotalAmount(BigDecimal.valueOf(invoice.totalAmount()));
        entity.setPeriod(period.toString());
        entity.setPaid(false);

        for (InvoiceLine line : invoice.lines()) {
            LineEntity lineEntity = new LineEntity();
            lineEntity.setId(UUID.randomUUID().toString().replace("-", ""));
            lineEntity.setLineId(line.index());
            lineEntity.setQuantity(BigDecimal.valueOf(line.quantity()));
            lineEntity.setStartDateTime(line.lineStart());
            lineEntity.setEndDateTime(line.lineEnd());
            lineEntity.setProduct(ProductType.valueOf(line.product()));
            lineEntity.setPrice(BigDecimal.valueOf(line.price()));
            lineEntity.setPriceList(line.priceList());
            lineEntity.setAmount(BigDecimal.valueOf(line.amount()));
            lineEntity.setInvoice(entity);
            entity.getLines().add(lineEntity);
        }

        invoiceRepository.save(entity);
    }

}
