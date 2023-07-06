package com.springbook.learningtest.jdk;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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

        Hello proxiedHello = new HelloUppercase(new HelloTarget()); // 프록시를 통해 target에 접근하도록 구성한다.
        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    @Test
    public void dynamicProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance( // -> 생성된 다이나믹 프록시 객체는 Hello 인터페이스를 구현하고 있으므로 Hello 타입으로 캐스팅해도 안전하다.
                getClass().getClassLoader(), // -> 동적으로 생성되는 다이나믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[] { Hello.class }, // -> 다이나믹 프록시가 구현할 인터페이스(여러 인터페이스 가능)
                new UppercaseHandler(new HelloTarget()) // -> 부가기능(프록시 로직)과 위임 코드를 담은 InvocationHandler
        );

        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }
}
