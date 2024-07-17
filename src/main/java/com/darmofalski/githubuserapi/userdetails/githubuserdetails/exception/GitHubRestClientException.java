package com.darmofalski.githubuserapi.userdetails.githubuserdetails.exception;

public class GitHubRestClientException extends RuntimeException {
    public GitHubRestClientException(String message, Exception exception) {
        super(message, exception);
    }
}
