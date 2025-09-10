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
            // NOT FOUND 404
            case USER_NOT_FOUND,
                 USER_NOT_FOUND_BY_USERNAME,
                 USER_NOT_FOUND_BY_EMAIL,
                 ANNOUNCEMENT_NOT_FOUND,
                 EVENT_TYPE_NOT_FOUND,
                 EVENT_NOT_FOUND,
                 COMPETITION_NOT_FOUND,
                 IMAGE_NOT_FOUND,
                 SESSION_NOT_FOUND,
                 SEASON_NOT_FOUND
                    -> HttpStatus.NOT_FOUND;

            // BAD REQUEST 400
            case USERNAME_NULL,
                 USERNAME_OR_EMAIL_NULL,
                 USERNAME_OR_PASSWORD_NULL,
                 PASSWORD_NULL,
                 PASSWORD_TOO_SHORT,
                 NEW_PASSWORD_NULL,
                 OLD_PASSWORD_INCORRECT,
                 INVALID_USERNAME_OR_PASSWORD,
                 PASSWORDS_DO_NOT_MATCH,
                 OLD_AND_NEW_PASSWORD_SAME,
                 EMAIL_NULL,
                 SEASON_NAME_NULL_OR_BLANK,
                 EVENT_TYPE_NAME_CANNOT_BE_NULL_OR_BLANK,
                 IMAGE_CANNOT_BE_NULL,
                 SESSION_DATES_CANNOT_BE_NULL,
                 SESSION_START_DATE_AFTER_END_DATE,
                 SESSION_TITLE_BLANK_OR_NULL,
                 SESSION_SPEAKER_NAME_NULL_OR_BLANK,
                 SESSION_TYPE_NOT_VALID,
                 SEASON_START_DATE_AFTER_END_DATE,
                 EMPTY_COMPETITOR,
                 VALIDATION_ERROR,
                 BAD_REQUEST
                    -> HttpStatus.BAD_REQUEST;

            // UNAUTHORIZED 401
            case USER_NOT_AUTHORIZED,
                 UNAUTHORIZED
                    -> HttpStatus.UNAUTHORIZED;

            // FORBIDDEN 403
            case FORBIDDEN,
                 USER_DOESNT_HAVE_ROLE
                    -> HttpStatus.FORBIDDEN;

            // CONFLICT 409
            case USER_ALREADY_EXISTS,
                 ROLE_ALREADY_EXISTS,
                 IMAGE_ALREADY_ADDED,
                 SEASON_NAME_ALREADY_EXISTS,
                 SEASON_ALREADY_CONTAINS_EVENT,
                 EVENT_IS_IN_ANOTHER_SEASON
                    -> HttpStatus.CONFLICT;

            // SERVICE UNAVAILABLE 503
            case SERVICE_UNAVAILABLE
                    -> HttpStatus.SERVICE_UNAVAILABLE;

            // INTERNAL SERVER ERROR 500
            case INTERNAL_SERVER_ERROR,
                 DATABASE_ERROR
                    -> HttpStatus.INTERNAL_SERVER_ERROR;

            // DEFAULT 400
            default -> HttpStatus.BAD_REQUEST;
        };
    }

}
