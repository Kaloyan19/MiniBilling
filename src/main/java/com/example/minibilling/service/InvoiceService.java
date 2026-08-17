package com.example.minibilling.service;

import com.example.minibilling.model.domain.Invoice;
import com.example.minibilling.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices(String sortBy) {
        List<Invoice> invoices = invoiceRepository.findAll();

        if ("reference".equals(sortBy)) {
            return invoices.stream()
                    .sorted(Comparator.comparing(Invoice::reference))
                    .toList();
        } else if ("consumer".equals(sortBy)) {
            return invoices.stream()
                    .sorted(Comparator.comparing(Invoice::consumer))
                    .toList();
        }

        return invoices;
    }

    public List<Invoice> getInvoicesByReference(String reference) {
        return invoiceRepository.findByUserReference(reference);
    }
}