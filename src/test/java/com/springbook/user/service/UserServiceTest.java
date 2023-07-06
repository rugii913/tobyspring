package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

import static com.springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
    List<User> users; // 테스트 픽스처

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "abc1@spring.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "abc2@spring.com"),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "abc3@spring.com"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "abc4@spring.com"),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "abc5@spring.com")
        );
    }

    @Test
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        // boolean upgraded로 어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드될 것인가 아닌가를 지정한다.
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel()); // 다음 레벨이 무엇인지는 Level에게 물어보면 된다.
        } else {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel()); // 업그레이드가 일어나지 않았는지 확인
        }
    }

    private void checkLevel(User user, Level expectedLevel) { // checkLevelUpgrade()로 개선하기 전 코드
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4); // GOLD 레벨 // -> GOLD 레벨이 이미 지정된 User라면 레벨을 초기화화지 않아야 한다.
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);  // -> 레벨이 비어있는 사용자, 로직에 따라 등록 중에 BASIC 레벨이 설정되어야 한다.

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        // DB에 저장된 결과를 가져와서 확인
        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    @Test // cf. p.388 5-56 문제점 - 롤백해도 메일은 이미 보냈음
    public void upgradeAllOrNothing() throws Exception {
        // 예외를 발생시킬 네 번째 사용자의 id를 넣어서 테스트용 UserService 대용 객체를 생성함
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao); // userDao를 수동으로 DI
        testUserService.setTransactionManager(transactionManager); // dataSource도 수동으로 DI
        testUserService.setMailSender(mailSender);

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try { // TestUserService는 업그레이드 작업 중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
            testUserService.upgradeLevels();
            // upgradeLevels()가 정상적으로 수행되면 "TestUserServiceException expected" 메시지와 함께 테스트 실패
            // upgradeLevels() 메서드 작업 중에 예외가 터져야 아래 fail(~) 부분이 실행되지 않는다.
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
            // TestUserService가 던져주는 예외를 잡고 아무 것도 안 해서, 정상 흐름인 듯이 계속 진행되도록 한다.
            // 그 외의 예외가 발생하면 그냥 평범하게 예외가 터지면서 테스트 실패
        }

        // 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었는지 확인
        checkLevelUpgraded(users.get(1), false);
    }
    
    static class TestUserService extends UserService { // 테스트에서만 사용할 내부 스태틱 클래스
        private String id;

        public TestUserService(String id) { // 예외를 발생시킬 User 객체의 id를 지정할 수 있게 만든다.
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) { // UserService의 메서드를 재정의
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            // -> 지정된 id를 가진 User 객체를 발견하면 예외를 던져서 작업을 강제로 중단시킨다.
            super.upgradeLevel(user); // 나머지는 피상속 메서드를 그대로 따라감
        }
    }

    static class TestUserServiceException extends RuntimeException { // 테스트용 예외
    }
}