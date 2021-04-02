package com.z2m2020.herostory.login.db;

public interface IUserDao {
    /**
     * 根据用户名获取实体
     * @param userName 用户名
     * @return 实体
     */        //getByUserName
    UserEntity getUserByName(String userName);

    /**
     * 添加用户实体
     * @param newEntity 实体
     */
    void insertInto(UserEntity newEntity);

}
