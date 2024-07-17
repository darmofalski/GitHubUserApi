package com.darmofalski.githubuserapi.userdetails.calculator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTest {

    @Test
    void checkResult() {
        double result = Calculator.calculate(1, 1);

        double expectedResult = 6 / (double) 1 * (2 + 1);
        assertThat(result).isCloseTo(expectedResult, offset(0.0001d));
    }

    @Test
    void checkResult_zeroFollowers() {
        assertThrows(CalculationException.class, () -> Calculator.calculate(0, 1));
    }

}
