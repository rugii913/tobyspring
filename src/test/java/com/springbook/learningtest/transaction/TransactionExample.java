package com.springbook.learningtest.transaction;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class TransactionExample {

    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        dao.deleteAll();
        this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0);
        this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10);
        this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40);
    }

    @Test
    void transactionExample() throws SQLException {
        Connection c = dataSource.getConnection();

        c.setAutoCommit(false); // -> 트랜잭션 시작
        try {
            PreparedStatement st1 = c.prepareStatement(
                    "insert into users(id, name, password, level, login, recommend) values ('gyumee', '박성철', 'springno1', 1, 1, 0)");
            st1.executeUpdate();

            PreparedStatement st2 = c.prepareStatement("asdf"); // 에러가 날 수밖에 없는 SQL
            st2.executeUpdate();

            c.commit(); // -> 트랜잭션 커밋
        } catch (Exception e) {
            c.rollback(); // -> 트랜잭션 롤백
            // 트랜잭션이 존재하는 범위는 c.setAutoCommit(false);부터 c.rollback();까지
            // 트랜잭션이 시작하는 곳과 끝나는 곳을 지정하는 것을 트랜잭션 경계설정이라고 한다.
        } finally {
            c.close();
            // JDBC의 트랜잭션은 하나의 Connection을 가져와 사용하다가 닫는 사이에서 일어난다.
            // 트랜잭션의 시작과 종료는 Connection 오브젝트를 통해 이뤄지기 때문이다.
        }

        Assertions.assertThat(dao.getAll().size()).isEqualTo(0);
    }

    // 5-38 upgradeLevels()가 가져야할 트랜잭션 경계설정 구조
    public void upgradeLevels() throws Exception {
        // (1) DB Connection 생성
        // (2) 트랜잭션 시작
        try {
            // (3) DAO 메서드 호출
            // (4) 트랜잭션 커밋
        } catch (Exception e) {
            // (5) 트랜잭션 롤백
            throw e;
        } finally {
            // (6) DB Connection 종료
        }
    }
}
