package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.results.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400
    @ExceptionHandler(SuperSkyLabException.class)
    public ResponseEntity<ErrorResult> handleSuperSkyLabException(SuperSkyLabException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult(
                message, HttpStatus.BAD_REQUEST
        ));
    }

    // 400
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResult> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult(
                ex.getMessage(), HttpStatus.BAD_REQUEST
        ));
    }

    // 400
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResult> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult(
                ex.getMessage(), HttpStatus.BAD_REQUEST
        ));
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResult> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResult(
                ex.getMessage(), HttpStatus.NOT_FOUND
        ));
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResult(
                "Sistem tarafında beklenmedik bir hata oluştu: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        ));
    }
}