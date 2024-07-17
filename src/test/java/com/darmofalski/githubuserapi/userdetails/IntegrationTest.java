package com.darmofalski.githubuserapi.userdetails;

import com.darmofalski.githubuserapi.configuration.exceptionhandling.ErrorResponse;
import com.darmofalski.githubuserapi.requestcounter.RequestCounter;
import com.darmofalski.githubuserapi.requestcounter.RequestCounterRepository;
import com.darmofalski.githubuserapi.requestcounter.RequestCounterService;
import com.darmofalski.githubuserapi.userdetails.githubuserdetails.GitHubRestClient;
import com.darmofalski.githubuserapi.userdetails.githubuserdetails.GitHubUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.offset;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RequestCounterRepository repository;

    @SpyBean
    private GitHubRestClient gitHubRestClient;

    @SpyBean
    private RequestCounterService requestCounterService;

    @BeforeEach
    void clearRequestCounter() {
        repository.deleteAll();
    }

    @Test
    void checkStatusCode_realIntegration() {
        var responseEntity = restTemplate.getForEntity("/users/octocat", UserDetails.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void checkResponse_unknownLogin_realIntegration() {
        var responseEntity = restTemplate.getForEntity("/users/unknown-login", ErrorResponse.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(404);
        var errorResponse = responseEntity.getBody();
        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.message()).isEqualTo("Login does not exist");

        var requestCounters = repository.findAll();
        assertThat(requestCounters.size()).isEqualTo(0);
    }

    @Test
    void checkRequestCounter_newLogin() {
        doReturn(createDummyUser("test-login", 1))
                .when(gitHubRestClient).getUserDetails(eq("test-login"));
        restTemplate.getForEntity("/users/test-login", UserDetails.class);

        var requestCounters = repository.findAll();

        assertThat(requestCounters.toArray()).containsExactly(new RequestCounter("test-login", 1L));
    }

    @Test
    void checkRequestCounter_existingLogin() {
        doReturn(createDummyUser("test-login", 1))
                .when(gitHubRestClient).getUserDetails(eq("test-login"));

        restTemplate.getForEntity("/users/test-login", UserDetails.class);
        restTemplate.getForEntity("/users/test-login", UserDetails.class);

        var requestCounters = repository.findAll();
        assertThat(requestCounters.size()).isEqualTo(1);

        assertThat(requestCounters.getFirst()).isEqualTo(new RequestCounter("test-login", 2L));
    }

    @Test
    void checkRequestCounter_multipleLogin() {
        doReturn(createDummyUser("test-login-1", 1))
                .when(gitHubRestClient).getUserDetails(eq("test-login-1"));
        doReturn(createDummyUser("test-login-2", 1))
                .when(gitHubRestClient).getUserDetails(eq("test-login-2"));

        restTemplate.getForEntity("/users/test-login-1", UserDetails.class);
        restTemplate.getForEntity("/users/test-login-2", UserDetails.class);

        var requestCounters = repository.findAll();
        assertThat(requestCounters.toArray())
                .containsExactly(new RequestCounter("test-login-1", 1L),
                        new RequestCounter("test-login-2", 1L));
    }


    @Test
    void checkResponse_calculationException() {
        doReturn(createDummyUser("calculation-problem", 0))
                .when(gitHubRestClient).getUserDetails(eq("calculation-problem"));

        var responseEntity = restTemplate.getForEntity("/users/calculation-problem", ErrorResponse.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(500);

        var errorResponse = responseEntity.getBody();
        assertThat(errorResponse.status()).isEqualTo(500);
        assertThat(errorResponse.message()).isEqualTo("Can't calculate the value for user");

        var requestCounters = repository.findAll();
        assertThat(requestCounters.size()).isEqualTo(0);
    }

    @Test
    void checkStatusCode_requestCounterFailed() {
        doReturn(createDummyUser("test-login", 1))
                .when(gitHubRestClient).getUserDetails(eq("test-login"));
        doThrow(new RuntimeException("Can't do request counter update"))
                .when(requestCounterService).updateRequestCounter(eq("test-login"));

        var responseEntity = restTemplate.getForEntity("/users/test-login", UserDetails.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody().login()).isEqualTo("test-login");
    }

    @Test
    void checkUserDetailsMapping() {
        doReturn(createDummyUser("test-login", 2))
                .when(gitHubRestClient).getUserDetails(eq("test-login"));

        var responseEntity = restTemplate.getForEntity("/users/test-login", UserDetails.class);

        var userDetails = responseEntity.getBody();
        assertThat(userDetails.login()).isEqualTo("test-login");
        assertThat(userDetails.name()).isEqualTo("test-login");
        assertThat(userDetails.type()).isEqualTo("User");
        assertThat(userDetails.avatarUrl()).isEqualTo("avatar");
        assertThat(userDetails.createdAt()).isEqualTo(LocalDateTime.of(2022, 1, 1, 1, 1));
        assertThat(userDetails.calculations()).isCloseTo(6 / (double) 2 * (1 + 2), offset(0001d));
        assertThat(responseEntity.getBody().login()).isEqualTo("test-login");
    }

    private GitHubUserDetails createDummyUser(String login, int followers) {
        return new GitHubUserDetails(2L, login, login, "User", "avatar", LocalDateTime.of(2022, 1, 1, 1, 1), followers, 1);
    }
}
