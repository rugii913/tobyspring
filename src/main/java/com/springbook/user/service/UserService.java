package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /*
    // upgradeLevels() 리팩토링 전
    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            Boolean changed = null; // 레벨의 변화가 있는지 확인하는 플래그
            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) { // BASIC 레벨 업그레이드 작업
                user.setLevel(Level.SILVER);
                changed = true; // 레벨 변경 플래그 설정
            } else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) { // SILVER 레벨 업그레이드 작업
                user.setLevel(Level.GOLD);
                changed = true;
            } else if (user.getLevel() == Level.GOLD) {
                changed = false; // GOLD 레벨은 변경이 일어나지 않는다.
            } else {
                changed = false; // 일치하는 조건이 없으면 변경 없음
            }

            if (changed) {
                userDao.update(user); // 레벨의 변경이 있는 경우에만 update() 호출
            }
        }
    }
    */
    public void upgradeLevels() { // 기본 작업 흐름만 남겨둔 upgradeLevels() - 가장 추상적인 레벨
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) { // 레벨별로 구분해서 조건을 판단
            case BASIC: return (user.getLogin() >= 50);
            case SILVER: return (user.getRecommend() >= 30);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
            // -> 현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킨다. 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다.
        }
    }

    private void upgradeLevel(User user) {
        // 작업 1. 사용자의 레벨을 다음 단계로 바꿔 줌
        user.upgradeLevel(); // (1) User의 내부 정보 변경을 User가 스스로 다룸 (2) 레벨 순서와 다음 단계 레벨이 무엇인지 결정하는 것은 Level enum이 맡고 있음
        // 작업 2. 변경사항을 DB에 업데이트
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
