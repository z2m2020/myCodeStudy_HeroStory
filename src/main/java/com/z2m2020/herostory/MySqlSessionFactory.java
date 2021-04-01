package com.z2m2020.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MySql 会话工厂
 */
public final class MySqlSessionFactory {
    static private final Logger LOGGER= LoggerFactory.getLogger(MySqlSessionFactory.class);

    static private SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化构造器
     */
    private MySqlSessionFactory(){

    }

    public static void init() {
        try{
            _sqlSessionFactory=(new SqlSessionFactoryBuilder()).build(
                    Resources.getResourceAsStream("MyBatisConfig.xml")
            );

            //测试数据库连接
            SqlSession temSession=openSession();

            temSession.getConnection()
                    .createStatement()
                    .execute("SELECT -1");
            temSession.close();

            LOGGER.error("mySql 数据库连接测试成功");
        }catch (Exception ex){
            //记录错误日志
            LOGGER.error(ex.getMessage(),ex);
        }
    }

    static public SqlSession openSession(){
        if(null==_sqlSessionFactory){
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
