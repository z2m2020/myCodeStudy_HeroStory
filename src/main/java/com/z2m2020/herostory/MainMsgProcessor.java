package com.z2m2020.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.z2m2020.herostory.cmdhandler.CmdHandlerFactory;
import com.z2m2020.herostory.cmdhandler.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * 创建一个单线程的线程池
     */
    private final ExecutorService _es= Executors.newSingleThreadExecutor((newRunnable)->{
        Thread newThread=new Thread(newRunnable);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });

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
     * 处理runnable 实例
     * @param r
     */
    public void process(Runnable r){
        if(null==r){
            return;
        }
        _es.submit(r);
    }

    /**
     * 处理消息
     * @param ctx
     * @param msg
     */
     public void process(ChannelHandlerContext ctx, Object msg){

        if(null==ctx || null== msg){
            return;
        }

        final Class<?> msgClazz=msg.getClass();

        LOGGER.info("收到客户端消息,msgClazz={} ,msg={} ",
                msg.getClass().getSimpleName(),
                msg
        );
        //封装到一个县城中
        _es.submit(()->{
            try {
                //获取处理命令
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler= CmdHandlerFactory.create(msgClazz);
                if(null!=cmdHandler){
                    cmdHandler.handle(ctx,cast(msg));
                }
            }catch (Exception ex){
                //记录错误日志
                LOGGER.error(ex.getMessage(),ex);
            }
        });
    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg){
        if(null==msg){
            return null;

        }else{
            return (TCmd) msg;
        }
    }

}
