package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

public class SelectHeroCmdHandler implements ICmdHandler<GameMsgProtocol.SelectHeroCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.SelectHeroCmd selectHeroCmd) {

    }
}
