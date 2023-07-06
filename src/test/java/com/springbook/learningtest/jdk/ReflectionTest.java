package com.springbook.learningtest.jdk;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionTest {

    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        // length()
        assertThat(name.length()).isEqualTo(6);

        Method lengthMethod = String.class.getMethod("length"); // 메서드 이름이 length이고 파라미터는 없는 것
        assertThat((Integer) lengthMethod.invoke(name)).isEqualTo(6);

        // charAt()
        assertThat(name.charAt(0)).isEqualTo('S');

        Method charAtMethod = String.class.getMethod("charAt", int.class); // 메서드 이름이 charAt이고 파라미터가 int 딱 한 개인 것
        assertThat((Character) charAtMethod.invoke(name, 0)).isEqualTo('S');
    }

    @Test
    public void simpleProxy() { // 클라이언트 역할을 하는 테스트
        Hello hello = new HelloTarget(); // -> target은 인터페이스를 통해 접근하는 습관을 들일 것
        assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
        assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
        assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    }
}
