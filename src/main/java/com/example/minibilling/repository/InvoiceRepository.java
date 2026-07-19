package com.example.minibilling.repository;

import com.example.minibilling.model.domain.Invoice;
import com.example.minibilling.model.domain.InvoiceLine;
import com.example.minibilling.model.entity.InvoiceEntity;
import com.example.minibilling.repository.jpa.InvoiceEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvoiceRepository {

    private final InvoiceEntityRepository invoiceEntityRepository;

    public InvoiceRepository(InvoiceEntityRepository invoiceEntityRepository) {
        this.invoiceEntityRepository = invoiceEntityRepository;
    }

    public void save(InvoiceEntity invoice) {
        invoiceEntityRepository.save(invoice);
    }

    public InvoiceEntity findByUserReferenceAndPeriod(String reference, String period) {
        return invoiceEntityRepository.findByUserReferenceAndPeriod(reference, period);
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
