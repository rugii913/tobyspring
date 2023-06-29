package com.springbook.user.dao;

public class DaoFactory { //설계도 역할을 하게 됨
    //==> (1)UserDao 생성 및 (2)ConnectionMaker 구현체 제공(생성자 주입)하여 의존관계 설정하는 책임이 클라이언트로부터 넘어왔음
    //==> 어플리케이션 컴포넌트 역할을 하는 오브젝트들과 - 어플리케이션의 구조를 결정하는 오브젝트 - 가 분리됨
    
    public UserDao userDao() {
        ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao userDao = new UserDao(connectionMaker);
        return userDao;
        //팩토리의 메서드는 UserDao 타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정한다. - 어떤 전략을 주입할까
    }
}
