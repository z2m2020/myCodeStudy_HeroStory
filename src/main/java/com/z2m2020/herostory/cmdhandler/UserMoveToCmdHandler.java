package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.Broadcaster;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        //
        //用户移动
        //

        //从session取回userID
        Integer UserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();

        //非空判断,避免服务器崩掉
        if (null == UserId) {
            return;
        }

//        GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();


        //用户id从哪里获取?

        //从session中拿id
        // --在用户入场的时候,把它的id赛到与之关联的信道中去
        resultBuilder.setMoveUserId(UserId);

        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
        //消息封装好了,继续添加decoder和encoder
        final GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

    }
}
