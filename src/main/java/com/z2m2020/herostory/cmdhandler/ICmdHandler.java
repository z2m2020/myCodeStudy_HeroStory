package com.z2m2020.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理命令
     * @param ctx channel上下文
     * @param cmd 封装后的命令
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
