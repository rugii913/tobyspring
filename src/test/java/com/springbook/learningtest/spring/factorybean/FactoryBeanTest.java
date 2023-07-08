package com.springbook.learningtest.spring.factorybean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration // -> 설정파일 이름을 지정하지 않으면 경로 + 클래스이름 + "-context.xml"이 디폴트로 사용된다.
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
//        Object message = context.getBean("message"); // -> 받아올 때 bean의 타입을 지정하지 않으면 Object로만 받아올 수 있다.
//        assertThat(message).isExactlyInstanceOf(Message.class); // -> 타입 확인
//        assertThat(((Message) message).getText()).isEqualTo("Factory Bean"); // -> 설정과 기능 확인

        Message message = context.getBean("message", Message.class); // bean의 타입을 지정하면 해당 타입으로 바로 꺼낼 수 있다.
        // -> FactoryBean 인터페이스를 구현할 클래스(여기서는 MessageFactoryBean)을 스프링 빈으로 만들어두면
        //    getObject()라는 메서드가 생성해주는 객체가 실제 빈 객체로 대치된다.
        assertThat(message).isExactlyInstanceOf(Message.class);
        assertThat(message.getText()).isEqualTo("Factory Bean");
    }

    @Test
    public void getFactoryBean() {
        Object factory = context.getBean("&message"); // -> &가 붙고 안 붙고에 따라 getBean() 메서드가 돌려주는 객체가 달라진다.
        assertThat(factory).isInstanceOf(FactoryBean.class);
        assertThat(factory).isExactlyInstanceOf(MessageFactoryBean.class);
    }
}
