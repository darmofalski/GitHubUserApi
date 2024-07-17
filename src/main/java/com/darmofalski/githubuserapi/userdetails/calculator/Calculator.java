package com.darmofalski.githubuserapi.userdetails.calculator;

public class Calculator {

    public static double calculate(int followers, int public_repos) {

        if (followers == 0) {
            throw new CalculationException("Number of followers is 0, can't do the calculations");
        }
        return 6 / (double) followers * (2 + public_repos);
    }
}
