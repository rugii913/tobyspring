package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
    private DataSource dataSource; // Connection을 생성할 때 사용할 DataSource를 DI 받는다.

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*
    p.367, 5-41~44 관련 문제점: JDBC 트랜잭션 API, JdbcTemplate과 동기화하는 API로 인해 JDBC 기술을 사용하는 DAO에 의존하게 된다.
    */

    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false); // DB 커넥션을 생성하고 트랜잭션을 시작한다. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
        // DataSourceUtils: DB 커넥션 생성과 동기화를 함께 해주는 유틸리티 메서드(동기화에 사용하도록 저장소에 바인딩도 해줌)

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            c.commit(); // -> 정상적으로 작업을 마치면 트랜잭션 커밋

        } catch (Exception e) {
            c.rollback(); // -> 예외가 발생하면 롤백한다.
            throw e;

        } finally {
            DataSourceUtils.releaseConnection(c, dataSource); // -> 스프링 유틸리티 메서드를 이용해 DB 커넥션을 안전하게 닫는다.
            // 동기화 작업 종료 및 정리
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

    /*
        작성된 코드를 살펴볼 때는 다음과 같은 질문을 할 필요가 있다.
        - 코드에 중복된 부분은 없는가?
        - 코드가 무엇을 하는 것인지 이해하기 불편하지 않은가?
        - 코드가 자신이 있어야할 자리에 있는가?
        - 앞으로 변경이 일어난다면 어떤 것이 있을 수 있고, 그 변화에 쉽게 대응할 수 있게 작성되어 있는가?
     */
}
