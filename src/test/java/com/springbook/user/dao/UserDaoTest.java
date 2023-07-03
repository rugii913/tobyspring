package com.springbook.user.dao;

import com.springbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@SpringBootTest // -> 이걸 붙여서 해결해도 된다.
@ExtendWith(SpringExtension.class) // -> SpringExtension integrates the Spring TestContext Framework into JUnit 5's Jupiter programming model.
@ContextConfiguration(locations = "/applicationContext.xml")
// -> @ExtendWith와 @ContextConfiguration 사용해서 어플리케이션 컨텍스트 관리 가능
// cf. @RunWith deprecated - https://youngminz.netlify.app/posts/toby-spring-boot-in-2021
@DirtiesContext // 테스트 메서드에서 어플리케이션 컨텍스트의 구성이나 상태를 변경한다는 것을 테스트 컨텍스트 프레임워크에 알려준다.
class UserDaoTest {

    @Autowired // UserDao 타입 빈을 직접 DI 받는다.
    private UserDao dao;
    // User 픽스처
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach // JUnit 제공 어노테이션, @Test 메서드가 실행되기 전에 먼저 실행돼야 하는 메서드를 정의
    public void setUp() { // dao를 context에서 DL 하던 것 제거하고, 필드에서 바로 DI 받음
        this.user1 = new User("gyumee", "박성철", "springno1");
        this.user2 = new User("leegw700", "이길원", "springno2");
        this.user3 = new User("bumjin", "박범진", "springno3");

        DataSource dataSource // 테스트에서 UserDao가 사용할 DataSource 오브젝트를 직접 생성한다.
                = new SingleConnectionDataSource("jdbc:mysql://localhost/testdb",
                "spring",
                "book",
                true);
        dao.setDataSource(dataSource);
    }

    @Test
    void addAndGet() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        // 첫 번째 User의 id로 get()을 실행하면, 첫 번째 User의 값을 가진 오브젝트를 돌려주는지 확인
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