package com.example.minibilling.repository;

import com.example.minibilling.model.domain.Invoice;
import com.example.minibilling.model.domain.InvoiceLine;
import com.example.minibilling.model.domain.ProductType;
import com.example.minibilling.model.entity.InvoiceEntity;
import com.example.minibilling.model.entity.LineEntity;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.jpa.InvoiceEntityRepository;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Repository
public class InvoiceRepository {

    private final InvoiceEntityRepository invoiceEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public InvoiceRepository(InvoiceEntityRepository invoiceEntityRepository,
                             UserEntityRepository userEntityRepository) {
        this.invoiceEntityRepository = invoiceEntityRepository;
        this.userEntityRepository = userEntityRepository;
    }

    public void save(Invoice invoice, String reference, YearMonth period) {
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

        invoiceEntityRepository.save(entity);
    }

    public Invoice findByUserReferenceAndPeriod(String reference, String period) {
        InvoiceEntity entity = invoiceEntityRepository.findByUserReferenceAndPeriod(reference, period);
        if (entity == null) return null;
        return toDomain(entity);
    }

    public Invoice toDomain(InvoiceEntity entity) {
        List<InvoiceLine> lines = entity.getLines().stream()
                .map(l -> new InvoiceLine(
                        l.getLineId(),
                        l.getQuantity().doubleValue(),
                        l.getStartDateTime(),
                        l.getEndDateTime(),
                        l.getProduct().name(),
                        l.getPrice().doubleValue(),
                        l.getPriceList(),
                        l.getAmount().doubleValue()
                ))
                .toList();

        return new Invoice(
                entity.getDateTime(),
                entity.getNumber(),
                entity.getUser().getName(),
                entity.getUser().getReference(),
                entity.getTotalAmount().doubleValue(),
                lines
        );
    }
}
