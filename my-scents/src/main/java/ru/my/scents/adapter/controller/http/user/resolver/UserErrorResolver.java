package ru.my.scents.adapter.controller.http.user.resolver;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import ru.my.scents.adapter.controller.http.user.response.AlertResponse;

@ControllerAdvice
public class UserErrorResolver {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<AlertResponse> handleIllegalArgumentException(Throwable ex, HttpServletRequest request) {

        AlertResponse errResponse = AlertResponse.builder()
                .httpCode(HttpStatus.BAD_REQUEST.name())
                .path(request.getRequestURI())
                .msg(ex.getMessage())
                .timestamp(Instant.now().toEpochMilli())
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<AlertResponse> handleSystemExceptions(Throwable ex, HttpServletRequest request) {
        AlertResponse errResponse = AlertResponse.builder()
                .httpCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .path(request.getRequestURI())
                .msg(ex.getMessage())
                .timestamp(Instant.now().toEpochMilli())
                .build();

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}