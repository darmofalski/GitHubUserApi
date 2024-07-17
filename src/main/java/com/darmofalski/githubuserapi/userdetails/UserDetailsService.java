package com.darmofalski.githubuserapi.userdetails;

import com.darmofalski.githubuserapi.userdetails.calculator.Calculator;
import com.darmofalski.githubuserapi.userdetails.githubuserdetails.GitHubRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService {

    private final GitHubRestClient gitHubRestClient;

    @Autowired
    UserDetailsService(GitHubRestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }

    UserDetails getUserDetails(String login) {
        var gitHubUser = gitHubRestClient.getUserDetails(login);

        double calculations = Calculator.calculate(gitHubUser.followers(), gitHubUser.public_repos());

        return new UserDetails(gitHubUser.id(),
                gitHubUser.login(),
                gitHubUser.name(),
                gitHubUser.type(),
                gitHubUser.avatar_url(),
                gitHubUser.created_at(),
                calculations);
    }
}
