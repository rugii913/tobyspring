package com.springbook.user.dao;

import com.springbook.context.AppContext;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppContext.class)
@ActiveProfiles("test")
class UserDaoTest {

    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "abc1@spring.com");
        this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "abc2@spring.com");
        this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "abc3s@spring.com");
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
        checkSameUser(userget1, user1);

        // 두 번째 User도 같은 방법으로 검증
        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
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

    @Test
    public void getAll() {
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size()).isEqualTo(0); // 데이터가 없을 때는 크기가 0인 리스트 오브젝트가 리턴돼야 한다.

        dao.add(user1); // Id: gyumee
        List<User> users1 = dao.getAll();
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0)); // checkSameUser(): 내부에서 사용하는 메서드

        dao.add(user2); // Id: leegw700
        List<User> users2 = dao.getAll();
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3); // Id: bumjin
        List<User> users3 = dao.getAll();
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user3, users3.get(0)); // 오름차순 조회할 것 - user3의 id 값이 알파벳 순으로 가장 빠르므로, getAll()의 첫 번째 element여야 한다.
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
    }

    @Test
    public void update() {
        dao.deleteAll();

        dao.add(user1); // 수정할 사용자
        dao.add(user2); // 수정하지 않을 사용자

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);

        dao.update(user1); // 픽스처에 들어있는 user1 정보를 변경한 후 수정 메서드를 호출

        User user1update = dao.get(user1.getId());
        checkSameUser(user1, user1update);
        User user2same = dao.get(user2.getId());
        checkSameUser(user2, user2same);
    }

    @Test // 일반적으로 학습 테스트는 어플리케이션 코드 테스트와 분리해서 작성함에 유의
    public void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        // 테스트를 실패시키면 어떤 예외 클래스가 던져졌는지 확인할 수 있다.
        // assertThatThrownBy(() -> dao.add(user1)).isEqualTo(DataAccessException.class);
        assertThatThrownBy(() -> dao.add(user1)).isInstanceOf(DataAccessException.class);
        // isExactlyInstanceOf로 정확히 어떤 클래스의 인스턴스인지 테스트할 수 있다.
        assertThatThrownBy(() -> dao.add(user1)).isExactlyInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void sqlExceptionTranslate() {
        dao.deleteAll();

        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException ex) {
            SQLException sqlEx = (SQLException) ex.getRootCause(); // 중첩되어 있는 SQLException 가져오기
            SQLExceptionTranslator set = // 빈으로 등록된 dataSource로 SQLErrorCodeSQLExceptionTranslator 오브젝트 만들기
                    new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            // set.translate(null, null, sqlEx)하면 SQLException을 DataAccessException 타입의 예외로 변환해준다. // task, sql 파라미터는 null로 둬도 테스트에 지장 없음
            assertThat(set.translate(null, null, sqlEx)).isExactlyInstanceOf(DuplicateKeyException.class);
        }
    }

    // User 객체의 내용을 비교하는 검증 코드, 테스트에서 반복적으로 사용되므로 분리해놓았다.
    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }

    @Autowired
    DefaultListableBeanFactory bf;

    @Test
    public void beans() {
        for (String beanDefinitionName : bf.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName + "\t " + bf.getBean(beanDefinitionName).getClass().getName());
        }
    }
}