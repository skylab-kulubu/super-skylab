package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.results.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // 400
    @ExceptionHandler(SuperSkyLabException.class)
    public ResponseEntity<ErrorResult> handleSuperSkyLabException(SuperSkyLabException ex) {
        String messageKey = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult(
                translate(messageKey),
                HttpStatus.BAD_REQUEST
        ));
    }

    // 422
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResult> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResult(
                translate(ex.getMessage()),
                HttpStatus.UNPROCESSABLE_ENTITY
        ));
    }

    // 400
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResult> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResult(
                translate(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        ));
    }

    // 404 - Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResult> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResult(
                translate(ex.getMessage()),
                HttpStatus.NOT_FOUND
        ));
    }

    // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResult> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: ", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResult(
                translate("system.data.integrity.violation"),
                HttpStatus.CONFLICT
        ));
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleGeneralException(Exception ex) {
        log.error("An unexpected system error occurred: ", ex);

        String translatedGenericError = messageSource.getMessage(
                "system.unexpected.error",
                null,
                "An unexpected system error occurred.",
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResult(
                translatedGenericError,
                HttpStatus.INTERNAL_SERVER_ERROR
        ));
    }

    private String translate(String messageKey) {
        if (messageKey == null || messageKey.isEmpty()) {
            return "An unknown error occurred.";
        }
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }
}