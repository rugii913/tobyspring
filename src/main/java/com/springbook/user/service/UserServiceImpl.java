package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userService")
public class UserServiceImpl implements UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    @Autowired
    private UserDao userDao;
    @Autowired
    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

    //--------------------
    // DAO 메서드와 1:1 대응되는 CRUD 메서드이지만 add()처럼 단순 위임 이상의 로직을 가질 수 있다.
    @Override
    public User get(String id) {return userDao.get(id);}
    @Override
    public List<User> getAll() {return userDao.getAll();}
    @Override
    public void deleteAll() {userDao.deleteAll();}
    @Override
    public void update(User user) {userDao.update(user);}
    //--------------------

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
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
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");

        this.mailSender.send(mailMessage);
    }

    /*
        작성된 코드를 살펴볼 때는 다음과 같은 질문을 할 필요가 있다.
        - 코드에 중복된 부분은 없는가?
        - 코드가 무엇을 하는 것인지 이해하기 불편하지 않은가?
        - 코드가 자신이 있어야할 자리에 있는가?
        - 앞으로 변경이 일어난다면 어떤 것이 있을 수 있고, 그 변화에 쉽게 대응할 수 있게 작성되어 있는가?
     */

    public static class TestUserService extends UserServiceImpl { // 테스트에서만 사용할 내부 스태틱 클래스
        private String id = "madnite1"; // -> 테스트 픽스처의 users(3)의 id 값을 고정시켜버렸다.

        @Override
        protected void upgradeLevel(User user) { // UserService의 메서드를 재정의
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            // -> 지정된 id를 가진 User 객체를 발견하면 예외를 던져서 작업을 강제로 중단시킨다.
            super.upgradeLevel(user); // 나머지는 피상속 메서드를 그대로 따라감
        }

        @Override
        public List<User> getAll() { // -> 읽기전용 트랜잭션의 대상인 get으로 시작하는 메서드를 재정의
            for (User user : super.getAll()) {
                super.update(user); // -> 쓰기 시도를 하면 읽기전용 속성으로 인해 예외가 발생해야 한다.
            }
            return null; // -> 메서드가 끝나기 전에 예외가 발생할 것이므로 반환값은 별 의미는 없다.
        }
    }

    static class TestUserServiceException extends RuntimeException { // 테스트용 예외
    }
}
