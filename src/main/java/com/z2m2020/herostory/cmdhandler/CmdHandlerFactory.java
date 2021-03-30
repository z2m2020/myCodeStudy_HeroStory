package com.z2m2020.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.z2m2020.herostory.msg.GameMsgProtocol;


import java.util.HashMap;
import java.util.Map;

public final class CmdHandlerFactory {
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
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
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
