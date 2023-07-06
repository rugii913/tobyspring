package com.springbook.learningtest.spring.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {

    String text;

    // 객체를 생성할 때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해서 대신 DI 받을 수 있게 한다.
    // 주입된 정보는 객체 생성 중에 사용된다.
    public void setText(String text) {
        this.text = text;
    }

    @Override
    // 실제 빈으로 사용될 객체를 직접 생성한다.
    // 코드를 사용하므로 복잡한 방식의 객체 생성과 초기화 작업도 가능하다.
    // -> 방금 만든 Message 클래스처럼 생성자가 아닌 static 메서드를 사용해서 생성해야하는 경우도 가능하다.
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    @Override
    // getObject() 메서드가 반환하는 객체가 싱글톤인지를 알려준다.
    // 이 팩토리 빈은 매번 요청할 때마다 새로운 객체(newMessage(~))를 만드므로 false를 반환.
    // 이것은 팩토리 빈의 동작 방식에 관한 설정이고,
    // 만들어진 빈 객체는 싱글톤으로 스프링이 관리해줄 수 있다.
    public boolean isSingleton() {
        return false;
    }
}
