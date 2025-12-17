package com.shijam.jpajoinentitypattern.exception;

import com.shijam.jpajoinentitypattern.error.BusinessException;
import com.shijam.jpajoinentitypattern.error.ErrorCode;
import com.shijam.jpajoinentitypattern.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
/*
Handled Exceptions:
    - BusinessException
    - MethodArgumentNotValidException
    - ConstraintViolationException
    - HttpMessageNotReadableException
    - MissingServletRequestParameterException
    - HttpRequestMethodNotSupportedException
    - NoResourceFoundException
    - Exception
 */
public class GeneralExceptionHandler
{

    //Handle BusinessException e.g.:
    //      - PRODUCT_NOT_FOUND
    //      - VALIDATION_ERROR
    //      - CONFLICT
    @ExceptionHandler(BusinessException.class) // 400/404
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex ,
            HttpServletRequest request
    ) {
        HttpStatus status = switch (ex.getCode()) {
            case PRODUCT_NOT_FOUND, CATEGORY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(status).body(
                ErrorResponse.of("Business Exception",
                        ex.getCode(),
                        request.getRequestURI(),
                        List.of(ex.getMessage())));
    }

    //Handle validation errors for requset body
    @ExceptionHandler(MethodArgumentNotValidException.class) //400
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of("validation error",
                        ErrorCode.VALIDATION_ERROR,
                        request.getRequestURI(),
                        validationErrors));
    }

    //ConstraintViolationException : @Validated on path/query
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of("validation error",
                        ErrorCode.VALIDATION_ERROR,
                        request.getRequestURI(),
                        details));
    }

    //Malformed JSON : JSON parse errors
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                 ErrorResponse.of("Malformed JSON request",
                        ErrorCode.VALIDATION_ERROR,
                        request.getRequestURI(),
                List.of(ex.getMessage()))); //"Malformed JSON request",
    }

    //Missing part of request param
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        String detail = "Required query parameter '" + ex.getParameterName() + "' is missing";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(
                        "Missing request parameter",
                        ErrorCode.VALIDATION_ERROR,
                        request.getRequestURI(),
                        List.of(detail)
                )
        );
    }
    // Exception: Request method ... is not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) //405
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        String supported = ex.getSupportedHttpMethods() != null
                ? ex.getSupportedHttpMethods().toString()
                : "N/A";

        String detail = "Method " + ex.getMethod()
                + " is not supported for this endpoint. Supported methods: " + supported;

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        ErrorResponse.of(
                                "HTTP method not allowed",
                                ErrorCode.VALIDATION_ERROR,
                                request.getRequestURI(),
                                List.of(detail)
                        )
                );
    }

    // NoResourceFoundException , like double slash in uri
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        "Resource not found",
                        ErrorCode.NOT_FOUND,
                        request.getRequestURI(),
                        List.of("Invalid API path")
                ));
    }

    //Catch-all handler for unexpected exceptions
    @ExceptionHandler(Exception.class) //500
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of("Internal server error",
                        ErrorCode.INTERNAL_ERROR,
                        request.getRequestURI(),
                        List.of("Unexpected error occurred.")));
    }


}
