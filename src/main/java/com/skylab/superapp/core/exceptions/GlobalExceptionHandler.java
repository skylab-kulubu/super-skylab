package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.results.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResult> handleValidationException(ValidationException ex) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult(
                ex.getMessage(), HttpStatus.BAD_REQUEST
       ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResult> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResult(
                ex.getMessage(), HttpStatus.NOT_FOUND
        ));
    }


}
