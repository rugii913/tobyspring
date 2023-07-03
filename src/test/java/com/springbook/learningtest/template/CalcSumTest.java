package com.springbook.learningtest.template;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcSumTest {
    Calculator calculator;
    String numFilepath;

    @BeforeEach
    void setUp() { // 픽스처
        this.calculator = new Calculator();
        this.numFilepath = getClass().getResource("/numbers.txt").getPath();
    }

    @Test
    void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(this.numFilepath)).isEqualTo(15);
    }

    @Test
    void multiplyOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(this.numFilepath)).isEqualTo(120);
    }

    @Test
    void concatenateOfNumbers() throws IOException {
        assertThat(calculator.concatenate(this.numFilepath)).isEqualTo("12345");
    }
}
