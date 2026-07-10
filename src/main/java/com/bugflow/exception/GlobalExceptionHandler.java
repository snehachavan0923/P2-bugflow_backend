package com.bugflow.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }
    
    @ExceptionHandler(
            org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request) {

        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }
    
    @ExceptionHandler(OrganizationSuspendedException.class)
    public ResponseEntity<ApiError> handleOrganizationSuspended(
            OrganizationSuspendedException ex,
            HttpServletRequest request) {

        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.FORBIDDEN.value(),
                        "Organization Suspended",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(com.bugflow.exception.SubscriptionLimitExceededException.class)
    public ResponseEntity<ApiError> handleSubscriptionLimitExceeded(
            com.bugflow.exception.SubscriptionLimitExceededException ex,
            HttpServletRequest request) {

        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(com.bugflow.exception.SubscriptionExpiredException.class)
    public ResponseEntity<ApiError> handleSubscriptionExpired(
            com.bugflow.exception.SubscriptionExpiredException ex,
            HttpServletRequest request) {

        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.FORBIDDEN.value(),
                        "Subscription Expired",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.FORBIDDEN
        );
    }
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "Something went wrong",
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<ApiError> handleCloudinary(
            CloudinaryException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Upload Failed",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}