package com.springbook;

import com.springbook.user.dao.ConnectionMaker;
import com.springbook.user.dao.DConnectionMaker;
import com.springbook.user.dao.DaoFactory;
import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        UserDao dao = new DaoFactory().userDao();
        //==> (1)UserDao 생성 및 (2)ConnectionMaker 구현체 제공(생성자 주입)하여 의존관계 설정하는 책임이 DaoFactory로 넘어감
        //==> 어플리케이션 컴포넌트 역할을 하는 오브젝트들과 - 어플리케이션의 구조를 결정하는 오브젝트 - 가 분리됨

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");
    }
}
