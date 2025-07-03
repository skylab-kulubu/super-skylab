package com.skylab.superapp.core.exceptions;

import com.skylab.superapp.core.results.ErrorCode;
import com.skylab.superapp.core.results.ErrorResult;
import com.skylab.superapp.core.results.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SuperSkyLabException.class)
    public ResponseEntity<Result> handleSuperSkyLabException(SuperSkyLabException exception, HttpServletRequest request){
        HttpStatus status = mapErrorCodeToStatus(exception.getErrorCode());
        return ResponseEntity.status(status)
                .body(new ErrorResult(
                        exception.getMessage(),
                        exception.getErrorCode(),
                        status,
                        request.getRequestURI()));
    }

    private HttpStatus mapErrorCodeToStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            //NOT FOUND 404
            case USER_NOT_FOUND_BY_EMAIL,
                 USER_NOT_FOUND_BY_USERNAME,
                 ANNOUNCEMENT_NOT_FOUND,
                 EVENT_TYPE_NOT_FOUND
                    -> HttpStatus.NOT_FOUND;


            //BAD REQUEST 400
            case PASSWORD_NULL,
                 USERNAME_OR_EMAIL_NULL,
                 INVALID_USERNAME_OR_PASSWORD
                    -> HttpStatus.BAD_REQUEST;


            //UNAUTHORIZED 401
            case USER_NOT_AUTHORIZED,
                 UNAUTHORIZED
                    -> HttpStatus.UNAUTHORIZED;


            //CONFLICT 409
            case IMAGE_ALREADY_ADDED
                    -> HttpStatus.CONFLICT;


            //DEFAULT 400
            default -> HttpStatus.BAD_REQUEST;
        };
    }


}
