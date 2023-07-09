package com.springbook.learningtest.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

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

    @Test
    public void pointcutAdvisor() { // ProxyFactoryBean은 앞에서 FactoryBean을 구현해서 만든 구현 클래스 TxFactoryBean과도 차이가 있다.
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget()); // -> target 설정

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut(); // -> 메서드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성
        pointcut.setMappedName("sayH*"); // -> 이름 비교조건 설정, sayH로 시작하는 모든 메서드를 선택하게 한다.

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        // -> 포인트컷과 어드바이스는 Advisor로 묶어서 한 번에 추가

        Hello proxiedHello = (Hello) pfBean.getObject(); // -> FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.

        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isNotEqualTo("THANK YOU TOBY"); // -> 메서드 이름이 포인트컷 조건에 맞지 않으므로, 부가 기능(대문자 변환)이 적용되지 않는다.
    }
}
