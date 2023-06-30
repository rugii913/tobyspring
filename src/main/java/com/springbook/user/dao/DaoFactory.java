package com.springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //--> 어플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {
    
    /*@Bean //--> 오브젝트 생성을 담당하는 IoC(Inversion of Control)용 메서드라는 표시
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }*/

    @Bean
    public AccountDao accountDao() {
        return new AccountDao(connectionMaker());
    }

    @Bean
    public MessageDao messageDao() {
        return new MessageDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker(); //분리해서 중복을 제거한 ConnectionMaker 타입 객체 생성 코드
    }
}
