package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;

class UserDaoTest {

    @Test //JUnit에게 테스트용 메서드임을 알려준다.
    void addAndGet() throws SQLException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);
        User user = new User();
        user.setId("gyumee");
        user.setName("박성철");
        user.setPassword("springno1");

        dao.add(user);

        User user2 = dao.get(user.getId());

        Assertions.assertThat(user2.getName()).isEqualTo(user.getName());
        Assertions.assertThat(user2.getPassword()).isEqualTo(user.getPassword());
    }

}