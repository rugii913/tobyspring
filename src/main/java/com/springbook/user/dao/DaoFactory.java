package com.springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
    
    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public AccountDao accountDao() {
        return new AccountDao(null);
    }

    @Bean
    public MessageDao messageDao() {
        return new MessageDao(null);
    }

    /*
    //p.138 리스트 1-43 관련 주석 처리
    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker(); //분리해서 중복을 제거한 ConnectionMaker 타입 객체 생성 코드
    }
    */

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/springbook");
        dataSource.setUsername("spring");
        dataSource.setPassword("book");

        return dataSource;
    }
}
