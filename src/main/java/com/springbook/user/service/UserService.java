package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
    private PlatformTransactionManager transactionManager;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) { // 프로퍼티 이름은 관례를 따라 transactionManager라고 만드는 것이 편리하다.
        this.transactionManager = transactionManager;
    }
    /*
    p.367, 5-41~44 관련 문제점: JDBC 트랜잭션 API, JdbcTemplate과 동기화하는 API로 인해 JDBC 기술을 사용하는 DAO에 의존하게 된다. 
    => 해결책: 트랜잭션 서비스 추상화 p.369~370 + 그림 5-6 스프링의 트랜잭션 추상화 계층
    => 5-45로 해결 => 5-46 구현 클래스를 알지 못하도록 트랜잭션 매니저를 빈으로 분리
    */

    public void upgradeLevels() throws Exception {
        // DI 받은 트랜잭션 매니저를 공유해서 사용한다. 멀티스레드 환경에서도 안전하다.
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        
        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            this.transactionManager.commit(status);

        } catch (Exception e) {
            this.transactionManager.rollback(status);
            throw e;
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
