package com.example.minibilling.controllers;

import com.example.minibilling.model.domain.Invoice;
import com.example.minibilling.model.entity.InvoiceEntity;
import com.example.minibilling.repository.InvoiceRepository;
import com.example.minibilling.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class BillingController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepository;

    public BillingController(BillingService billingService, InvoiceRepository invoiceRepository){
        this.billingService = billingService;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping("/{reference}")
    public ResponseEntity<Invoice> generateInvoice(
            @PathVariable String reference,
            @RequestParam int year,
            @RequestParam int month) throws Exception {

        YearMonth yearMonth = YearMonth.of(year, month);
        return billingService.generateAndSaveInvoice(reference, yearMonth)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Invoice> getInvoice(
            @PathVariable String reference,
            @RequestParam int year,
            @RequestParam int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        InvoiceEntity entity = invoiceRepository.findByUserReferenceAndPeriod(
                reference, yearMonth.toString());

        if (entity == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(invoiceRepository.toDomain(entity));
    }
}
