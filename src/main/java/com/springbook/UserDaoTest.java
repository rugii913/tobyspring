package com.springbook;

import com.springbook.user.dao.ConnectionMaker;
import com.springbook.user.dao.DConnectionMaker;
import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        UserDao dao = new UserDao(); //필요 없어진 MainTest의 코드
        ConnectionMaker connectionMaker = new DConnectionMaker();
        //UserDao와 ConnectionMaker 구현체의 !!오브젝트 간 관계!!를 맺는 책임이 UserDao의 클라이언트로 넘어옴
//        ConnectionMaker connectionMaker = new NConnectionMaker();
        //리스트 1-13 오브젝트 간 관계를 맺는 책임이 클라이언트로 넘어오면서
        //UserDao는 자신의 관심사이자 책임에 집중할 수 있음
        //DB 커넥션을 가져오는 방법을 변경해도(ConnectionMaker의 구현체를 변경해도) UserDao 코드는 바뀌지 않는다.

        UserDao dao = new UserDao(connectionMaker);
        //1. UserDao 생성
        //2. 사용할 ConnectionMaker 타입의 오브젝트 제공(인자로 넘김)
        //==> 두 오브젝트 사이의 의존관계 설정 효과

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
