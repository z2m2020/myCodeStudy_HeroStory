package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户攻击处理器
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    /**
     * 日至对象
     * @param ctx
     * @param userAttkCmd
     */
    static private final Logger LOGGER= LoggerFactory.getLogger(UserAttkCmdHandler.class);
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd userAttkCmd) {
        LOGGER.info("UserAtta");
    }
}
