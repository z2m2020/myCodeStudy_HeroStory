package com.z2m2020.herostory.login;

import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.z2m2020.herostory.MySqlSessionFactory;
import com.z2m2020.herostory.async.AsyncOperationProcessor;
import com.z2m2020.herostory.async.IAsyncOperation;
import com.z2m2020.herostory.login.db.IUserDao;
import com.z2m2020.herostory.login.db.UserEntity;
import com.z2m2020.herostory.util.RedisUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

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

    public void userLogin(String userName, String password, Function<UserEntity,Void> callback){
        if(null==userName||
           null== password){
           return;
        }

        AsyncGetUserEntity asynOp=new AsyncGetUserEntity(userName,password){
            @Override
            public void doFinish() {
                if (null!=callback){
                    callback.apply(this.getUserEntity());
                }
            }
        };

        AsyncOperationProcessor.getInstance().process(asynOp);



    }

    /**
     * 更新redis中的用户基本信息,
     * @param userEntity 用户实体
     */
    private void updateBasicInfoInRedis(UserEntity userEntity){
        if(null==userEntity){
            return;
        }
        try(final Jedis redis = RedisUtil.getJedis()){
            JSONObject jsonObj=new JSONObject();
            jsonObj.put("userName",userEntity.userName);
            jsonObj.put("heroAvatar",userEntity.heroAvatar);


            redis.hset("User_"+userEntity.userId,"BasicInfo",jsonObj.toJSONString());


        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);

        }

    }

    private class AsyncGetUserEntity implements IAsyncOperation{
        /**
         * 用户名
         */
        private final String _userName;
        /**
         * 密码
         */
        private final String _password;
        /**
         * 用户实体
         */
        private UserEntity _userEntity;

        AsyncGetUserEntity(String userName,String password){
            _userName=userName;
            _password=password;
        }

        UserEntity getUserEntity(){
            return _userEntity;
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession=MySqlSessionFactory.openSession()){
                LOGGER.info("当前进程 {}",Thread.currentThread().getName());
                //获取 DAO
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                //获取实体类
                UserEntity userEntity=dao.getUserByName(_userName);

                if(null!=userEntity){
                    if(!_password.equals(userEntity.password)){
                        throw new RuntimeException("密码错误");
                    }
                }else{
                    userEntity=new UserEntity();
                    userEntity.userName=_userName;
                    userEntity.password=_password;
                    userEntity.heroAvatar="Hero_Shaman";

                    dao.insertInto(userEntity);

                }

                LoginService.getInstance().updateBasicInfoInRedis(userEntity);
                _userEntity=userEntity;

            }catch (Exception ex){
                //记录错误日志
                LOGGER.error(ex.getMessage(),ex);

            }
        }

        @Override
        public void doFinish() {

        }
    }

}
