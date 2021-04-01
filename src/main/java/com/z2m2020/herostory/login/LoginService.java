package com.z2m2020.herostory.login;

import com.z2m2020.herostory.MySqlSessionFactory;
import com.z2m2020.herostory.login.db.IUserDao;
import com.z2m2020.herostory.login.db.UserEntity;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录服务
 */
public class LoginService {
    /**
     * 日志对象
     *
     *
     */
    private static final Logger LOGGER= LoggerFactory.getLogger(LoginService.class);
    /**
     * 单例对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 私有化构造器
     */
    private LoginService(){}

    /**
     * 获取单例服务
     *
     */

    static public LoginService getInstance(){
        return _instance;
    }

    public UserEntity userLogin(String userName,String password){
        if(null==userName||
           null== password){
           return null;
        }

        try (SqlSession mySqlSession=MySqlSessionFactory.openSession()){

            //获取 DAO
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);
            //获取实体类
            UserEntity userEntity=dao.getUserByName(userName);

            if(null!=userEntity){
                if(!password.equals(userEntity.password)){
                    throw new RuntimeException("密码错误");
                }
            }else{
                userEntity=new UserEntity();
                userEntity.userName=userName;
                userEntity.password=password;
                userEntity.heroAvatar="Hero_Shaman";

                dao.insertInto(userEntity);

            }
            return userEntity;
        }catch (Exception ex){
            //记录错误日志
            LOGGER.error(ex.getMessage(),ex);
            return null;
        }


    }

}
