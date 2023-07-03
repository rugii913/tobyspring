package com.springbook.learningtest.template;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CalcSumTest {

    @Test
    void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(getClass().getResource("/numbers.txt").getPath());
        Assertions.assertThat(sum).isEqualTo(10);
    }
}
