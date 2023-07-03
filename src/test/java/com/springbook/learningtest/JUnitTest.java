package com.springbook.learningtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/junit.xml")
public class JUnitTest {

    @Autowired
    ApplicationContext context;
    // 테스트 컨텍스트가 매번 주입해주는 어플리케이션 컨텍스트는 항상 같은 객체인지 테스트로 확인해본다.

    static JUnitTest testObject;
    static Set<JUnitTest> testObjects = new HashSet<>();
    static ApplicationContext contextObject = null;

    @Test
    public void test1() { // 시간날 때 assertj 살펴보고 테스트 발전시켜 보기
        assertThat(this).isNotSameAs(testObject);
        assertThat(this).isNotIn(testObjects);
        testObject = this;
        testObjects.add(this);

        assertThat(contextObject).satisfiesAnyOf(
                contextObject -> assertThat(contextObject).isNull(),
                contextObject -> assertThat(contextObject).isEqualTo(this.context)
        );
        contextObject = this.context;

        System.out.println(this.context);
        System.out.println(this.context);
        System.out.println(this.context);
    }

    @Test
    public void test2() {
        assertThat(this).isNotSameAs(testObject);
        assertThat(this).isNotIn(testObjects);
        testObject = this;
        testObjects.add(this);

        assertThat(contextObject).satisfiesAnyOf(
                contextObject -> assertThat(contextObject).isEqualTo(null),
                contextObject -> assertThat(contextObject).isEqualTo(this.context)
        );
        contextObject = this.context;
    }

    @Test
    public void test3() {
        assertThat(this).isNotSameAs(testObject);
        assertThat(this).isNotIn(testObjects);
        testObject = this;
        testObjects.add(this);

        assertThat(contextObject).satisfiesAnyOf(
                contextObject -> assertThat(contextObject).isNull(),
                contextObject -> assertThat(contextObject).isEqualTo(this.context)
        );
        contextObject = this.context;
    }
}
