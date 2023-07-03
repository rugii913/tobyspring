package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserDaoTest { // UserDaoTest를 Bean으로 만들지는 않았기 때문에 자동으로 주입받지 못하고, context에서 불러옴

    private UserDao dao; // setUp() 메서드에서 만드는 객체를 테스트 메서드에서 사용할 수 있도록 인스턴스 변수로 선언

    @BeforeEach // JUnit 제공 어노테이션, @Test 메서드가 실행되기 전에 먼저 실행돼야 하는 메서드를 정의
    public void setUp() { // 각 테스트 메서드에 반복적으로 나타났던 dao를 가져오는 코드를 제거하고 별도의 메서드로 추출
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        this.dao = context.getBean("userDao", UserDao.class);
    }

    @Test
    void addAndGet() throws SQLException {
        // 중복되지 않는 값을 가진 두 User 오브젝트를 준비
        User user1 = new User("gyumee", "박성철", "springno1");
        User user2 = new User("leegw700", "이길원", "springno2");

        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        // 첫 번째 User의 id로 get()을 실행하면,
        // 첫 번째 User의 값을 가진 오브젝트를 돌려주는지 확인
        User userget1 = dao.get(user1.getId());
        assertThat(userget1.getName()).isEqualTo(user1.getName());
        assertThat(userget1.getPassword()).isEqualTo(user1.getPassword());

        // 두 번째 User도 같은 방법으로 검증
        User userget2 = dao.get(user2.getId());
        assertThat(userget2.getName()).isEqualTo(user2.getName());
        assertThat(userget2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    public void count() throws SQLException {
        User user1 = new User("gyumee", "박성철", "springno1");
        User user2 = new User("leegw700", "이길원", "springno2");
        User user3 = new User("bumjin", "박범진", "springno3");

        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        assertThat(dao.getCount()).isEqualTo(3);
    }

    @Test
    public void getUserFailure() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> dao.get("unknown_id"))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}