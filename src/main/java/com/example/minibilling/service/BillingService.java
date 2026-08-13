package com.example.minibilling.service;

import com.example.minibilling.exception.UserNotFoundException;
import com.example.minibilling.model.domain.*;
import com.example.minibilling.repository.InvoiceRepository;
import com.example.minibilling.repository.PriceRepository;
import com.example.minibilling.repository.ReadingRepository;
import com.example.minibilling.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BillingService {

    private final DistributionService distributionService;
    private final UserRepository userRepository;
    private final ReadingRepository readingRepository;
    private final PriceRepository priceRepository;
    private final AtomicInteger invoiceCounter = new AtomicInteger(10000);
    private final InvoiceRepository invoiceRepository;

    public BillingService(UserRepository userRepository, ReadingRepository readingRepository, PriceRepository priceRepository,
                          InvoiceRepository invoiceRepository, DistributionService distributionService){
        this.userRepository = userRepository;
        this.readingRepository = readingRepository;
        this.priceRepository = priceRepository;
        this.invoiceRepository = invoiceRepository;
        this.distributionService = distributionService;
    }

    public Optional<Invoice> generateAndSaveInvoice(String reference, LocalDate from, LocalDate to) {
        Optional<Invoice> invoice = generateInvoice(reference, from, to);
        if (invoice.isEmpty()) return Optional.empty();
        invoiceRepository.save(invoice.get(), reference, from + "_" + to);
        return invoice;
    }

    private Optional<Invoice> generateInvoice(String reference, LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new DateTimeException("Началната дата трябва да е преди крайната!");
        }

        User user = userRepository.findByReference(reference);
        if (user == null) throw new UserNotFoundException(reference);

        List<Reading> readings = findReadings(user, from, to);
        if (readings.size() < 2) return Optional.empty();

        List<PricePeriod> prices = findPrices(user);
        List<InvoiceLine> lines = createInvoiceLines(readings, prices, user);
        Invoice inv = buildInvoice(user, lines);
        return Optional.of(buildInvoice(user, lines));
    }

    private List<Reading> findReadings(User user, LocalDate from, LocalDate to) {
        return readingRepository.findByCustomerReference(user.reference())
                .stream()
                .filter(r -> !r.date().toLocalDate().isBefore(from))
                .filter(r -> !r.date().toLocalDate().isAfter(to))
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
        int index = 1;

        for (int i = 0; i < readings.size() - 1; i++) {
            Reading from = readings.get(i);
            Reading to = readings.get(i + 1);
            double totalQuantity = round2(to.meterReading() - from.meterReading());

            List<PricePeriod> productPrices = prices.stream()
                    .filter(p -> p.product() == from.product())
                    .toList();

            List<DistributionLine> distributed = distributionService.distribute(
                    from.date(), to.date(), totalQuantity, productPrices);

            for (DistributionLine dl : distributed) {
                lines.add(new InvoiceLine(index++, money(dl.quantity()), dl.start(), dl.end(),
                        from.product().name(), "kW/h",
                        money(dl.price()),
                        user.priceListNumber(),
                        money(round2(dl.quantity() * dl.price())),
                        null, null));
            }
        }
        return lines;
    }

    // TODO: използва се за филтриране по продукт при допълнение 3
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
        List<InvoiceLine> taxLines = generateTaxLines(lines, user);

        List<InvoiceLine> allLines = new ArrayList<>(lines);
        allLines.addAll(taxLines);

        List<VatLine> vat = generateVatLines(allLines);

        BigDecimal totalAmount = money(round2(
                allLines.stream().mapToDouble(l -> l.amount().doubleValue()).sum()
        ));

        BigDecimal totalAmountWithVat = money(round2(
                totalAmount.doubleValue() + vat.stream().mapToDouble(VatLine::amount).sum()
        ));

        return new Invoice(
                OffsetDateTime.now(),
                String.valueOf(invoiceCounter.getAndIncrement()),
                user.name(),
                user.reference(),
                totalAmount,
                totalAmountWithVat,
                allLines,
                vat
        );
    }

    private List<InvoiceLine> generateTaxLines(List<InvoiceLine> lines, User user) {
        List<PricePeriod> scPrices = priceRepository.findByPriceListAndProduct(
                user.priceListNumber(), ProductType.STANDING_CHARGE);
        List<PricePeriod> cclPrices = priceRepository.findByPriceListAndProduct(
                user.priceListNumber(), ProductType.CCL);

        OffsetDateTime start = lines.get(0).lineStart();
        OffsetDateTime end = lines.get(lines.size()-1).lineEnd();

        List<InvoiceLine> taxes = new ArrayList<>();
        taxes.addAll(generateScLines(lines, scPrices, start, end));
        taxes.addAll(generateCclLines(lines, cclPrices, start, end));

        return assignIndexes(taxes, lines.size() + 1);
    }

    private List<InvoiceLine> generateScLines(List<InvoiceLine> lines,
                                              List<PricePeriod> scPrices,
                                              OffsetDateTime start,
                                              OffsetDateTime end) {
        List<InvoiceLine> result = new ArrayList<>();

        for (PricePeriod scPrice : scPrices) {
            OffsetDateTime scStart = scPrice.startDate().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
            OffsetDateTime scEnd = scPrice.endDate().atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

            OffsetDateTime lineStart = scStart.isBefore(start) ? start : scStart;
            OffsetDateTime lineEnd = scEnd.isAfter(end) ? end : scEnd;

            if (lineStart.isAfter(lineEnd)) continue;

            int days = (int) ChronoUnit.DAYS.between(lineStart.toLocalDate(), lineEnd.toLocalDate()) + 1;

            List<Integer> matchingLines = findMatchingLines(lines, lineStart, lineEnd);

            result.add(new InvoiceLine(
                    0, money(days), lineStart, lineEnd,
                    ProductType.STANDING_CHARGE.name(), "days",
                    money(scPrice.price()), 0,
                    money(round2(days * scPrice.price())),
                    "Standing charge", matchingLines
            ));
        }
        return result;
    }

    private List<InvoiceLine> generateCclLines(List<InvoiceLine> lines,
                                               List<PricePeriod> cclPrices,
                                               OffsetDateTime start,
                                               OffsetDateTime end) {
        double totalQuantity = lines.stream()
                .mapToDouble(l -> l.quantity().doubleValue())
                .sum();
        List<InvoiceLine> result = new ArrayList<>();

        distributionService.distribute(start, end, totalQuantity, cclPrices)
                .forEach(dl -> {
                    List<Integer> matchingLines = findMatchingLines(lines, dl.start(), dl.end());
                    result.add(new InvoiceLine(
                            0, money(dl.quantity()), dl.start(), dl.end(),
                            ProductType.CCL.name(), "kW/h",
                            money(dl.price()), 0,
                            money(round2(dl.quantity() * dl.price())),
                            "CCL", matchingLines
                    ));
                });
        return result;
    }

    private List<Integer> findMatchingLines(List<InvoiceLine> lines,
                                            OffsetDateTime start,
                                            OffsetDateTime end) {
        return lines.stream()
                .filter(l -> !l.lineStart().isAfter(end) && !l.lineEnd().isBefore(start))
                .map(InvoiceLine::index)
                .toList();
    }

    private List<InvoiceLine> assignIndexes(List<InvoiceLine> taxes, int startIndex) {
        int index = startIndex;
        List<InvoiceLine> indexed = new ArrayList<>();
        for (InvoiceLine tax : taxes) {
            indexed.add(new InvoiceLine(index++, tax.quantity(), tax.lineStart(),
                    tax.lineEnd(), tax.product(), tax.unit(), tax.price(),
                    tax.priceList(), tax.amount(), tax.name(), tax.lines()));
        }
        return indexed;
    }

    private List<VatLine> generateVatLines(List<InvoiceLine> allLines) {
        double vatPercentage = 20.0;

        List<Integer> allIndexes = allLines.stream()
                .map(InvoiceLine::index)
                .toList();

        double totalBase = allLines.stream()
                .mapToDouble(l -> l.amount().doubleValue())
                .sum();
        double vatAmount = round2(totalBase * vatPercentage / 100);

        return List.of(new VatLine(1, allIndexes, vatPercentage, vatAmount));
    }

    private double round2(double value){
        return Math.ceil(value * 100) / 100.00;
    }

    private double round3(double value){
        return Math.ceil(value * 1000) / 100.00;
    }

    private BigDecimal money(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
