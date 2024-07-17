package com.darmofalski.githubuserapi.userdetails;

import com.darmofalski.githubuserapi.configuration.exceptionhandling.ErrorResponse;
import com.darmofalski.githubuserapi.requestcounter.RequestCounterService;
import com.darmofalski.githubuserapi.userdetails.calculator.CalculationException;
import com.darmofalski.githubuserapi.userdetails.githubuserdetails.exception.LoginNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/users")
public class UserDetailsController {

    Logger logger = LoggerFactory.getLogger(UserDetailsController.class);
    private final RequestCounterService requestCounterService;
    private final UserDetailsService userDetailsService;


    UserDetailsController(RequestCounterService requestCounterService, UserDetailsService userDetailsService) {
        this.requestCounterService = requestCounterService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/{login}")
    UserDetails getUser(@PathVariable String login) {

        var userDetails = userDetailsService.getUserDetails(login);
        try {
            requestCounterService.updateRequestCounter(login);
        } catch (Exception e) {
            logger.warn("Request counter for login: '{}' was not updated", login, e);
        }
        return userDetails;
    }

    @ExceptionHandler({LoginNotFoundException.class})
    public ResponseEntity<ErrorResponse> loginNotFound(LoginNotFoundException e) {
        logger.atWarn().log("Login '{}' does not exist", e.getLogin());
        var errorResponse = new ErrorResponse("Login does not exist", NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler({CalculationException.class})
    public ResponseEntity<ErrorResponse> loginNotFound(CalculationException e) {
        logger.atError().log("Calculation problem", e);
        var errorResponse = new ErrorResponse("Can't calculate the value for user", INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, INTERNAL_SERVER_ERROR);
    }
}
