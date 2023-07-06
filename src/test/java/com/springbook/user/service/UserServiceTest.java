package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
@DirtiesContext // -> 컨텍스트의 DI 설정을 변경하는 테스트라는 것을 알려준다.
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
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
    public void upgradeLevels(){
        // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다. // -> this.로 필드 가져와서 쓰지 않음
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users); // mockUserDao를 직접 DI 해준다.
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated(); // -> mockUserDao에서 업데이트 결과를 가져온다.
        // 업데이트 횟수와 정보를 확인
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        // 메일 발송 관련 테스트는 기존 코드 그대로.
        List<String> request = mockMailSender.getRequests();
        assertThat(request.size()).isEqualTo(2);
        assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());
    }

    // id와 level을 확인하는 간단한 헬퍼 메서드
    private void checkUserAndLevel(User updatedUser, String expectedId, Level expectedLevel) {
        assertThat(updatedUser.getId()).isEqualTo(expectedId);
        assertThat(updatedUser.getLevel()).isEqualTo(expectedLevel);
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

    @Test
    public void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService); // userService 필드에 testUserService를 넣어서 예외 터뜨릴 것

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels(); // 트랜잭션 기능을 분리한 txUserService 객체를 거쳐서 예외 발생용 TestUserService가 호출될 것
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1), false);
    }
    
    static class TestUserService extends UserServiceImpl { // 테스트에서만 사용할 내부 스태틱 클래스
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

    static class MockUserDao implements UserDao { // UserServiceTest 전용이므로 스태틱 내부 클래스로 만들었다.(p.419)

        private List<User> users; // -> 레벨 업그레이드 후보 User 객체 목록
        private List<User> updated = new ArrayList<>(); // -> 업그레이드 대상 오브젝트를 저장해둘 목록

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        @Override
        public List<User> getAll() { // -> getAll()에 대해서는 stub으로서 동작
            return this.users;
        }

        @Override
        public void update(User user) { // -> update(~)에 대해서는 mock으로서 동작
            updated.add(user);
        }

        // 테스트에 사용되지 않는 메서드들
        @Override
        public void add(User user) { throw new UnsupportedOperationException(); }
        @Override
        public User get(String id) { throw new UnsupportedOperationException(); }
        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }
        @Override
        public Integer getCount() { throw new UnsupportedOperationException(); }
    }
}