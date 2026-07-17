package com.example.minibilling.service;

import com.example.minibilling.exception.UserNotFoundException;
import com.example.minibilling.model.domain.*;
import com.example.minibilling.repository.*;
import com.example.minibilling.validator.BillingDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadingRepository readingRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private BillingDataValidator validator;

    @InjectMocks
    private BillingService billingService;

    private User user;
    private Reading reading1;
    private Reading reading2;
    private PricePeriod price;

    @BeforeEach
    void setUp() {
        user = new User("Marko Boikov Tsvetkov", "1", 1);

        reading1 = new Reading("1", ProductType.GAS,
                OffsetDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(3)),
                1480.0);

        reading2 = new Reading("1", ProductType.GAS,
                OffsetDateTime.of(2024, 3, 17, 13, 20, 0, 0, ZoneOffset.ofHours(3)),
                1567.0);

        price = new PricePeriod(
                ProductType.GAS,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                1.8,
                1
        );
    }

    @Test
    void shouldGenerateInvoiceWithCorrectAmount() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(readingRepository.findByCustomerReference("1")).thenReturn(List.of(reading1, reading2));
        when(priceRepository.findByPriceList(1)).thenReturn(List.of(price));

        Optional<Invoice> result = billingService.generateInvoice("1", YearMonth.of(2024, 3));

        assertTrue(result.isPresent());
        assertEquals(156.6, result.get().totalAmount());
        assertEquals(1, result.get().lines().size());
        assertEquals(87.0, result.get().lines().get(0).quantity());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () ->
                billingService.generateInvoice("999", YearMonth.of(2024, 3)));
    }

    @Test
    void shouldReturnEmptyWhenNotEnoughReadings(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(readingRepository.findByCustomerReference("1"))
                .thenReturn(List.of(reading1));

        Optional<Invoice> result = billingService.generateInvoice("1", YearMonth.of(2024, 3));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowWhenNoPriceForPeriod(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(readingRepository.findByCustomerReference("1"))
                .thenReturn(List.of(reading1, reading2));
        when(priceRepository.findByPriceList(1)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () ->
                billingService.generateInvoice("1", YearMonth.of(2024, 3)));
    }

    @Test
    void shouldHandleUnorderedReadings(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(readingRepository.findByCustomerReference("1"))
                .thenReturn(List.of(reading2, reading1));
        when(priceRepository.findByPriceList(1)).thenReturn(List.of(price));

        Optional<Invoice> result = billingService.generateInvoice("1", YearMonth.of(2024, 3));

        assertTrue(result.isPresent());
        assertEquals(156.6, result.get().totalAmount());
    }
}
