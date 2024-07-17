package com.darmofalski.githubuserapi.configuration.exceptionhandling;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
class GlobalControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> defaultErrorHandler(HttpServletRequest req, Exception e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), INTERNAL_SERVER_ERROR.value()), INTERNAL_SERVER_ERROR);
    }
}