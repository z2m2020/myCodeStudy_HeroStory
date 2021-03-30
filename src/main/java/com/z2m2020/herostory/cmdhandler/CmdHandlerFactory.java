package com.z2m2020.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import com.z2m2020.herostory.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ParameterMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class CmdHandlerFactory {
    /**
     * 日至对象
     */
    static private final Logger LOGGER= LoggerFactory.getLogger(CmdHandlerFactory.class);

    /**
     * 命令处理器字典
     */
    static private Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory() {

    }

    /**
     * 初始化map
     */
    static public void init() {
        LOGGER.info("===命令与处理器关联建立完成===");
        //获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        //获取ICmdHandler 所有实现类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                packageName,
                true,
                ICmdHandler.class
        );


//        clazzSet.forEach(e->LOGGER.info(e.getName()));

        for (Class<?> handlerClazz : clazzSet) {
            if (null == handlerClazz || 0 != (handlerClazz.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }


            //获取方法数组
            Method[] methodArray = handlerClazz.getDeclaredMethods();
            //消息类型
            Class<?> msgClazz = null;
            for (Method currMethod :
                    methodArray) {
                if (null == currMethod || !currMethod.getName().equals("handle")) {
                    continue;
                }
                //获取函数参数类型数据
                Class<?>[] paramTypeArray = currMethod.getParameterTypes();

                if (paramTypeArray.length < 2 ||
                        paramTypeArray[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }

                msgClazz = paramTypeArray[1];
                break;
            }

            if (null == msgClazz) {
                continue;
            }

            try{
                ICmdHandler<?> newHandler=(ICmdHandler<?>)handlerClazz.newInstance();
                LOGGER.info("{}<==>{}",
                        msgClazz.getName(),
                        handlerClazz.getName());
                _handlerMap.put(msgClazz,newHandler);
            }catch (Exception ex){
                LOGGER.error(ex.getMessage(),ex);
            }
        }
    }
    /**
     * 创建命令修理器
     *
     * @param msgClazz
     * @return
     */
    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
        //重构之前的代码
//        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
//            return new UserEntryCmdHandler();
//        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
//          return new WhoElseIsHereHandler();
//        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
//          return new UserMoveToCmdHandler();
//        } else {
//            return null;
////        }
//    }
//}
