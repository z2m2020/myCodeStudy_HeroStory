package com.z2m2020.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.z2m2020.herostory.cmdhandler.CmdHandlerFactory;
import com.z2m2020.herostory.cmdhandler.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MainMsgProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER= LoggerFactory.getLogger(MainMsgProcessor.class);
    /**
     * 单例对象
     *
     */
    static private final MainMsgProcessor _instance=new MainMsgProcessor();

    /**
     * 私有化默认构造器
     */
    private MainMsgProcessor(){}

    /**
     * 获取单例对象
     * @return 单独对相关
     */
    static public MainMsgProcessor getInstance(){
        return _instance;
    }

    /**
     * 处理消息
     * @param ctx
     * @param msg
     */
    static public void process(ChannelHandlerContext ctx, Object msg){

        LOGGER.info("收到客户端消息,msgClazz={} ,msg={} ",
                msg.getClass().getSimpleName(),
                msg
        );


        /**
         *  得到其他玩家发来的用户信息,并转发
         */

        try {
            ICmdHandler<? extends GeneratedMessageV3> cmdHandler= CmdHandlerFactory.create(msg.getClass());
            if(null!=cmdHandler){
                cmdHandler.handle(ctx,cast(msg));
            }
        }catch (Exception ex){
            //记录错误日志
            LOGGER.error(ex.getMessage(),ex);
        }




    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg){
        if(null==msg){
            return null;

        }else{
            return (TCmd) msg;
        }
    }

}
