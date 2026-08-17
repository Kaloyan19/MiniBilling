package com.example.minibilling.controllers;

import com.example.minibilling.model.domain.Invoice;
import com.example.minibilling.model.entity.AccountEntity;
import com.example.minibilling.repository.InvoiceRepository;
import com.example.minibilling.repository.jpa.AccountEntityRepository;
import com.example.minibilling.service.BillingService;
import com.example.minibilling.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class BillingController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepository;
    private final AccountEntityRepository accountRepository;
    private final InvoiceService invoiceService;

    public BillingController(BillingService billingService, InvoiceRepository invoiceRepository,
                             AccountEntityRepository accountRepository, InvoiceService invoiceService){
        this.billingService = billingService;
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
        this.invoiceService = invoiceService;
    }

    @PostMapping("/{reference}")
    public ResponseEntity<Invoice> generateInvoice(
            @PathVariable String reference,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) throws IOException {

        return billingService.generateAndSaveInvoice(reference, from, to)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Invoice> getInvoice(
            @PathVariable String reference,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {

        Invoice invoice = invoiceRepository.findByUserReferenceAndPeriod(
                reference, from + "_" + to);
        if (invoice == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/my")
    public ResponseEntity<Invoice> getMyInvoice(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            Authentication authentication) {

        AccountEntity account = accountRepository.findByUsername(authentication.getName());
        if (account.getCustomerReference() == null) {
            return ResponseEntity.badRequest().build();
        }

        Invoice invoice = invoiceRepository.findByUserReferenceAndPeriod(
                account.getCustomerReference(), from + "_" + to);
        if (invoice == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/my")
    public ResponseEntity<Invoice> generateMyInvoice(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            Authentication authentication) {

        AccountEntity account = accountRepository.findByUsername(authentication.getName());
        if (account.getCustomerReference() == null) {
            return ResponseEntity.badRequest().build();
        }

        return billingService.generateAndSaveInvoice(
                        account.getCustomerReference(), from, to)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(sortBy));
    }

    @GetMapping("/by-reference")
    public ResponseEntity<List<Invoice>> getInvoicesByReference(
            @RequestParam String reference) {
        return ResponseEntity.ok(invoiceService.getInvoicesByReference(reference));
    }
}
