package com.z2m2020.herostory.model;

public class User {
    public Integer userId;
    public String heroAvatar;
    public final MoveState moveState=new MoveState();
    /**
     * 当前血量
     */
    public int currHp;

    public String userName;

    public User() {
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }
}
