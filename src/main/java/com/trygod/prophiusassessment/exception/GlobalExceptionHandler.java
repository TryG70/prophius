package com.trygod.prophiusassessment.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import java.util.*;

import static java.util.Optional.ofNullable;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LogManager.getLogger();

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiCallError<String>> handleNotFoundException(HttpServletRequest request, NotFoundException ex) {
        logger.error("NotFoundException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiCallError<>("Not found exception", Arrays.asList(ex.getMessage())));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiCallError<String>> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException ex) {
        logger.error("DataIntegrityViolationException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiCallError<>("Unique constraint violation occurred", Arrays.asList(Objects.requireNonNull(ex.getRootCause()).getMessage())));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiCallError<String>> handleValidationException(HttpServletRequest request, ValidationException ex) {
        logger.error("ValidationException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Validation exception", Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiCallError<String>> handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException ex) {
        logger.error("handleMissingServletRequestParameterException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Missing request parameter", Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiCallError<Map<String, String>>> handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        logger.error("handleMethodArgumentTypeMismatchException {}\n", request.getRequestURI(), ex);

        Map<String, String> details = new HashMap<>();
        details.put("paramName", ex.getName());
        details.put("paramValue", ofNullable(ex.getValue()).map(Object::toString).orElse(""));
        details.put("errorMessage", Objects.requireNonNull(ex.getRootCause()).getLocalizedMessage());

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Method argument type mismatch", Arrays.asList(details)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiCallError<Map<String, String>>> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        logger.error("handleMethodArgumentNotValidException {}\n", request.getRequestURI(), ex);

        List<Map<String, String>> details = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    Map<String, String> detail = new HashMap<>();
                    detail.put("objectName", error.getObjectName());
                    detail.put("errorMessage", error.getDefaultMessage());
                    if (error instanceof FieldError){
                        FieldError fieldError = (FieldError) error;
                        detail.put("field", fieldError.getField());
                        detail.put("rejectedValue", "" + fieldError.getRejectedValue());
                    }
                    details.add(detail);
                });

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Method argument validation failed", details));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiCallError<String>> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        logger.error("handleAccessDeniedException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiCallError<>("Access denied!", Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiCallError<String>> handleUsernameNotFoundException(HttpServletRequest request, UsernameNotFoundException ex) {
        logger.error("handleUsernameNotFoundException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>("Username not found!", Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiCallError<String>> handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex) {
        logger.error("handleBadCredentialsException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiCallError<>("Invalid Credentials!", Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiCallError<String>> handleInternalAuthenticationServiceException(HttpServletRequest request, InternalAuthenticationServiceException ex) {
        logger.error("handleInternalAuthenticationServiceException {}\n", request.getRequestURI(), ex);
        Throwable e = ex;
        int i = 0;
        do {
            i++;
            if(e.getCause() != null) {
                e = e.getCause();
            }
        } while (i <= 2 && e != null);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>("Authentication Error!", Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCallError<String>> handleInternalServerError(HttpServletRequest request, Exception ex) {
        logger.error("handleInternalServerError {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiCallError<>("Internal server error", Arrays.asList(ex.getMessage())));
    }


    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ApiCallError<T> {

        private String message;
        private List<T> details;

    }
}
