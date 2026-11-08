package com.example.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {
    private final Calculator calculator = new Calculator();

    @Test
    void addsNumbers() {
        assertEquals(5.0, calculator.add(2.0, 3.0));
    }

    @Test
    void subtractsNumbers() {
        assertEquals(1.0, calculator.subtract(3.0, 2.0));
    }

    @Test
    void multipliesNumbers() {
        assertEquals(6.0, calculator.multiply(2.0, 3.0));
    }

    @Test
    void dividesNumbers() {
        assertEquals(2.0, calculator.divide(6.0, 3.0));
    }

    @Test
    void rejectsDivisionByZero() {
        assertThrows(IllegalArgumentException.class, () -> calculator.divide(1.0, 0.0));
    }

    // @Test
    // void intentional() {
    //     assertEquals(5.0, calculator.add(2.0, 2.0));
    // }
}
