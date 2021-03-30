package com.z2m2020.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class GameMsgRecognizer {
    /**
     * 记录日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);
    /**
     * 消息编号->消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap=new HashMap<>();
    /**
     * 消息类-> 消息编号字典
     */
    static private final Map<Class<?>,Integer> _msgClazzAndMsgCodeMap=new HashMap<>();



    /**
     * 私有化构造器
     */
    private GameMsgRecognizer(){

    }


    /**
     * 初始化
     */
    static public void init(){

        LOGGER.info("===完成消息类与消息编号映射===");

        //获取内部类
        Class<?>[] innerClazzArray=GameMsgProtocol.class.getDeclaredClasses();

        for(Class<?> innerClazz:innerClazzArray){
            if(null==innerClazz ||
                    !GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                    continue;
            }

            String clazzName = innerClazz.getSimpleName();
            clazzName=clazzName.toLowerCase();

            for(GameMsgProtocol.MsgCode msgCode: GameMsgProtocol.MsgCode.values()){
                if(null==msgCode){
                    continue;
                }

                //获取消息编码
                String strMsgCode=msgCode.name();
                strMsgCode=strMsgCode.replaceAll("_","");
                strMsgCode=strMsgCode.toLowerCase();

                if(!strMsgCode.startsWith(clazzName)){
                    continue;
                }

                try{
                    Object returnObj=innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    //打印映射日志
                    LOGGER.info(
                            "{}<===>{}",
                            innerClazz.getName(),
                            msgCode.getNumber()
                    );

                    _msgCodeAndMsgObjMap.put(
                            msgCode.getNumber(),
                            (GeneratedMessageV3)returnObj
                    );

                    _msgClazzAndMsgCodeMap.put(
                            innerClazz,
                            msgCode.getNumber()
                    );

                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(),ex);
                }
            }

        }



//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE,GameMsgProtocol.UserEntryCmd.getDefaultInstance());
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE,GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE,GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
//
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class,GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class,GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class,GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class,GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
    }
    /**
     * 根据消息编号,获取消息构建器
     * @param msgCode 消息编号
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if(msgCode<0){
            return null;
        }

        final GeneratedMessageV3 defaultMsg = _msgCodeAndMsgObjMap.get(msgCode);

        if(null==defaultMsg){
            return null;
        }else{
            return defaultMsg.newBuilderForType();
        }
    }

    /**
     * 根据消息类获取消息编号
     * @param msgClazz
     * @return
     */
    static public int getMsgCodeByClazz(Class<?> msgClazz){
        if(null==msgClazz){
            return -1;
        }

        Integer msgCode=_msgClazzAndMsgCodeMap.get(msgClazz);
        if(null==msgCode){
            return -1;
        }else{
            return msgCode.intValue();
        }
    }
}
