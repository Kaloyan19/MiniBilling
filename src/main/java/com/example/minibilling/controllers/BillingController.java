package com.example.minibilling.controllers;

import com.example.minibilling.model.Invoice;
import com.example.minibilling.service.BillingService;
import com.example.minibilling.service.InvoiceFileWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Optional;

@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class BillingController {

    private final BillingService billingService;
    private final InvoiceFileWriter invoiceFileWriter;

    public BillingController(BillingService billingService, InvoiceFileWriter invoiceFileWriter){
        this.billingService = billingService;
        this.invoiceFileWriter = invoiceFileWriter;
    }

    @PostMapping("/{reference}")
    public ResponseEntity<Invoice> generateInvoice(
            @PathVariable String reference,
            @RequestParam int year,
            @RequestParam int month) throws Exception {

        YearMonth yearMonth = YearMonth.of(year, month);
        Invoice invoice = billingService.getOrGenerateInvoice(reference, yearMonth);

        if (invoice == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Invoice> getInvoice(
            @PathVariable String reference,
            @RequestParam int year,
            @RequestParam int month) throws Exception{

        YearMonth yearMonth = YearMonth.of(year, month);
        String consumer = billingService.findConsumerName(reference);
        Invoice invoice = invoiceFileWriter.read(consumer, reference, yearMonth);

        if (invoice == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(invoice);
    }
}
