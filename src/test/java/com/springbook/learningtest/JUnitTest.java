package com.springbook.learningtest;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnitTest {

    static JUnitTest testObject;
    static Set<JUnitTest> testObjects = new HashSet<>();

    @Test
    public void test1() {
        assertThat(this).isNotSameAs(testObject);
        assertThat(this).isNotIn(testObjects);
        testObject = this;
        testObjects.add(this);
    }

    @Test
    public void test2() {
        assertThat(this).isNotSameAs(testObject);
        assertThat(this).isNotIn(testObjects);
        testObject = this;
        testObjects.add(this);
    }

    @Test
    public void test3() {
        assertThat(this).isNotSameAs(testObject);
        assertThat(this).isNotIn(testObjects);
        testObject = this;
        testObjects.add(this);
    }
}
