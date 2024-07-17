package com.darmofalski.githubuserapi.userdetails.githubuserdetails.exception;

public class LoginNotFoundException extends RuntimeException {

    public String getLogin() {
        return login;
    }

    private final String login;

    public LoginNotFoundException(String message, String login, Exception e) {
        super(message, e);
        this.login = login;
    }
}
