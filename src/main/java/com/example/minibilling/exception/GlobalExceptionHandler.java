package com.example.minibilling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.DateTimeException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class) // BillingSErvice no user by refference
  public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(BillingDataException.class)
  public ResponseEntity<String> handleBillingData(BillingDataException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

  @ExceptionHandler(DateTimeException.class) // BillingService when from is after to
  public ResponseEntity<String> handleInvalidPeriod(DateTimeException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Невалиден период. Месецът трябва да е между 1 и 12.");
  }

  @ExceptionHandler(ImportException.class) // when csv is not valid
  public ResponseEntity<String> handleImportException(ImportException e){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }
}