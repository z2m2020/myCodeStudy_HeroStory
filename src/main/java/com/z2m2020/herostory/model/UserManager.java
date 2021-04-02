package com.z2m2020.herostory.model;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理
 */
public final class UserManager {

    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap=new ConcurrentHashMap<>();

    /**
     * 私有化默认构造器
     */
    private UserManager(){

    }

    /**
     * 添加用户
     * @param u User
     */
    static public void addUser(User u){
        if(null!=u) {
            _userMap.putIfAbsent(u.userId, u);
        }
    }

    /**
     * 移除用户
     * @param userId userId
     */
    static public void removeByUserId(Integer userId){
        if (null!=userId){
            _userMap.remove(userId);
        }
    }

    /**
     * 列表用户
     * @return User list
     */
    static public Collection<User> listUser(){
        return _userMap.values();
    }


    static public User getByUserId(int userId){
        return _userMap.get(userId);
    }

}
