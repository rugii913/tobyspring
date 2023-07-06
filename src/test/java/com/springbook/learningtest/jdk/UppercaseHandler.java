package com.springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

    Hello target;

    public UppercaseHandler(Hello target) {
        this.target = target; // 다이나믹 프록시로부터 전달받은 요청을 다시 target 객체에 위임해야 하므로 target 객체를 주입받아 둔다.(생성자 주입)
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String ret = (String) method.invoke(target, args); // -> target으로 위임, 인터페이스의 메서드 호출에 모두 적용된다.
        // -> toUpperCase()를 적용하기 위해 (String) 캐스팅함. Hello의 모든 메서드의 반환 타입은 String이므로 괜찮다.
        return ret.toUpperCase(); // -> 부가기능 제공
    }
}
