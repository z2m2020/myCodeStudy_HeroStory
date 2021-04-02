package com.z2m2020.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis 实用工具类
 */
public final class RedisUtil {

    /**
     * 日志对象
     */
    static private final Logger LOGGER= LoggerFactory.getLogger(RedisUtil.class);
    /**
     * Redis pool
     */
    static private JedisPool _jedisPool=null;

    /**
     * 私有化默认构造器
     */
    private RedisUtil(){}


    static public void init(){
        try {
            _jedisPool = new JedisPool("192.168.64.60", 6379);
            LOGGER.info("Redis 连接成功");
        }catch(Exception ex){
            // 记录错误日志
            LOGGER.error(ex.getMessage(),ex);
        }
    }


    /**
     * 获取Jedis实例
     *
     * @return redis实例
     */
    static public Jedis getJedis(){
        if(null==_jedisPool){
            throw new RuntimeException("_jedisPool 尚未初始化");

        }

        Jedis jedis =_jedisPool.getResource();
//        jedis.auth("root");

        return jedis;
    }

}
