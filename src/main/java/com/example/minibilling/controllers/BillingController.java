package com.example.minibilling.controllers;

import com.example.minibilling.model.Invoice;
import com.example.minibilling.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.YearMonth;
import java.util.Optional;

@RestController
@RequestMapping("/invoices")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService){
        this.billingService = billingService;
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Invoice> getInvoice(
            @PathVariable String reference,
            @RequestParam String period) {

        YearMonth yearMonth = YearMonth.parse(period.trim());
        Optional<Invoice> invoice = billingService.generateInvoice(reference, yearMonth);

        return invoice
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
