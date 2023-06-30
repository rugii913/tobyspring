package com.springbook.user.dao;

public class DaoFactory { //설계도 역할을 하게 됨
    //==> (1)UserDao 생성 및 (2)ConnectionMaker 구현체 제공(생성자 주입)하여 의존관계 설정하는 책임이 클라이언트로부터 넘어왔음
    //==> 어플리케이션 컴포넌트 역할을 하는 오브젝트들과 - 어플리케이션의 구조를 결정하는 오브젝트 - 가 분리됨
    //리스트 1-16 DAO 생성 메서드 추가로 중복 발생
    //리스트 1-17 ConnectionMaker 타입 객체 생성 코드 분리해서 중복 제거
    
    public UserDao userDao() {
        return new UserDao(connectionMaker()); //리스트 1-15 -> 리스트 1-16 인라인화
        //팩토리의 메서드는 UserDao 타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정한다. - 어떤 전략을 주입할까
    }

    public AccountDao accountDao() {
        return new AccountDao(connectionMaker());
    }

    public MessageDao messageDao() {
        return new MessageDao(connectionMaker());
    }

    private ConnectionMaker connectionMaker() {
        return new DConnectionMaker(); //분리해서 중복을 제거한 ConnectionMaker 타입 객체 생성 코드
    }
}
