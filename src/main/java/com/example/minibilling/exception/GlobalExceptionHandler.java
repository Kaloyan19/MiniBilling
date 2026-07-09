package com.example.minibilling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(BillingDataException.class)
  public ResponseEntity<String> handleBillingData(BillingDataException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<String> handleInvalidPeriod(DateTimeParseException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Невалиден формат на период. Използвайте: yyyy-MM");
  }
}