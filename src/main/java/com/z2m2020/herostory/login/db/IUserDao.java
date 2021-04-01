package com.z2m2020.herostory.login.db;

import org.apache.ibatis.annotations.Mapper;

//@Mapper
public interface IUserDao {
    /**
     * 根据用户名获取实体
     * @param userName
     * @return
     */        //getByUserName
    UserEntity getUserByName(String userName);

    /**
     * 添加用户实体
     * @param newEntity
     */
    void insertInto(UserEntity newEntity);

}
