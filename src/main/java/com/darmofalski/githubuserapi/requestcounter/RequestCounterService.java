package com.darmofalski.githubuserapi.requestcounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
public class RequestCounterService {

    Logger logger = LoggerFactory.getLogger(RequestCounterService.class);
    private final RequestCounterRepository requestCounterRepository;

    public RequestCounterService(RequestCounterRepository requestCounterRepository) {
        this.requestCounterRepository = requestCounterRepository;
    }

    @Transactional(isolation = REPEATABLE_READ)
    public void updateRequestCounter(String login) {
        var requestCounter = requestCounterRepository.findById(login);

        if (requestCounter.isPresent()) {
            incrementCounter(login, requestCounter.get());
        } else {
            createNewCounter(login);
        }
    }

    private void incrementCounter(String login, RequestCounter requestCounter) {
        requestCounter.incrementRequestCount();
        requestCounterRepository.save(requestCounter);
        logger.info("Login: '{}' - count incremented to {}", login, requestCounter.getRequestCount());
    }

    private void createNewCounter(String login) {
        RequestCounter newRequestCounter = new RequestCounter(login, 1L);
        requestCounterRepository.save(newRequestCounter);
        logger.info("New login: '{}', counter  created", login);
    }
}
