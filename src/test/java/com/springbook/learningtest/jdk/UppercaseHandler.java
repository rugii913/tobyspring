package com.springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

    Object target;

    public UppercaseHandler(Object target) {
        this.target = target; // 어떤 종류의 인터페이스를 구현한 target에도 적용 가능하도록 Object 타입으로 수정
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = (String) method.invoke(target, args);
        if (ret instanceof String && method.getName().startsWith("say")) {
            // -> 호출한 메서드의 리턴 타입이 String인 경우 && 메서드 이름이 say로 시작하는 경우 둘 다 만족할 때 부가기능 적용
            return ((String) ret).toUpperCase();
        } else { // 조건이 일치하지 않으면 target의 method 호출 결과를 그대로 반환
            return ret;
        }
    }
}
