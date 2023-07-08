package com.springbook.learningtest.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class DynamicProxyTest {

    @Test
    public void simpleProxy() {
        // JDK 동적 프록시 생성
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] { Hello.class }, new UppercaseHandler(new HelloTarget()));

        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    @Test
    public void proxyFactoryBean() { // ProxyFactoryBean은 앞에서 FactoryBean을 구현해서 만든 구현 클래스 TxFactoryBean과도 차이가 있다.
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget()); // -> target 설정
        pfBean.addAdvice(new UppercaseAdvice()); // -> 부가기능을 담은 어드바이스를 추가한다. 여러 개를 추가할 수도 있다.

        Hello proxiedHello = (Hello) pfBean.getObject(); // -> FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.

        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            // -> 리플렉션의 Method와 달리 메서드 실행 시 target 객체를 전달할 필요가 없다.
            //    MethodInvocation은 메서드 정보와 함께 target 객체를 알고 있기 때문이다.
            return ret.toUpperCase(); // -> 부가기능 적용
        }
    }
}
