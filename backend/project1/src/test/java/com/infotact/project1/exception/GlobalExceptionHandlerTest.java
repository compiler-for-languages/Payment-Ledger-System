package com.infotact.project1.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleBusinessException() {
        BusinessException exception = BusinessExceptions.invalidAmount();

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid amount. Amount must be greater than zero", response.getBody().getMessage());
    }
}
