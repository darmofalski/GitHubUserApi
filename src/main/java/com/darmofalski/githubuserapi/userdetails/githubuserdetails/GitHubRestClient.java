package com.darmofalski.githubuserapi.userdetails.githubuserdetails;

import com.darmofalski.githubuserapi.userdetails.githubuserdetails.exception.GitHubRestClientException;
import com.darmofalski.githubuserapi.userdetails.githubuserdetails.exception.LoginNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class GitHubRestClient {

    @Value("${github.url}")
    private String gitHubUrl;
    private final RestClient restClient;

    GitHubRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GitHubUserDetails getUserDetails(String login) {
        try {

            return restClient.get()
                    .uri(gitHubUrl + "/users/{login}", login)
                    .retrieve()
                    .body(GitHubUserDetails.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new LoginNotFoundException("Login '" + login + "' not found in GitHub API", login, e);
        } catch (Exception e) {
            throw new GitHubRestClientException("GitHub Client exception for login " + login, e);
        }
    }
}
