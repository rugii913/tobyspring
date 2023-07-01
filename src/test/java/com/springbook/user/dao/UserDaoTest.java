package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

class UserDaoTest {

    @Test
    void addAndGet() throws SQLException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);

        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        User user = new User("gyumee", "박성철", "springno1");

        dao.add(user);
        assertThat(dao.getCount()).isEqualTo(1);

        User user2 = dao.get(user.getId());

        assertThat(user2.getName()).isEqualTo(user.getName());
        assertThat(user2.getPassword()).isEqualTo(user.getPassword());
    }

}