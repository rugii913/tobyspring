package com.springbook.learningtest.spring.pointcut;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.assertj.core.api.Assertions.assertThat;

public class PointcutTest {

    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int com.springbook.learningtest.spring.pointcut.Target.minus(int, int) throws java.lang.RuntimeException)");

        // Target.minus()
        assertThat(pointcut.getClassFilter().matches(Target.class)
                && pointcut.getMethodMatcher()
                .matches(Target.class.getMethod("minus", int.class, int.class), null))
                .isEqualTo(true);

        // Target.plus()
        assertThat(pointcut.getClassFilter().matches(Target.class)
                && pointcut.getMethodMatcher()
                .matches(Target.class.getMethod("plus", int.class, int.class), null))
                .isEqualTo(false);

        // Bean.method()
        assertThat(pointcut.getClassFilter().matches(Target.class)
                && pointcut.getMethodMatcher()
                .matches(Target.class.getMethod("method"), null))
                .isEqualTo(false);
    }
}
