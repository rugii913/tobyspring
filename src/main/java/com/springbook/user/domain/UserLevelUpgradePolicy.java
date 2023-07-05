package com.springbook.user.domain;

public interface UserLevelUpgradePolicy {

    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
