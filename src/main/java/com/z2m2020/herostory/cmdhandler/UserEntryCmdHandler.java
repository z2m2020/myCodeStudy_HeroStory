package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.Broadcaster;
import com.z2m2020.herostory.model.User;
import com.z2m2020.herostory.model.UserManager;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd){
        if(null==ctx|| null==cmd){
            return;
        }
        int userID = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();
//
        //用户入场消息
        //
        User newUser = new User();
        newUser.userId = userID;
        newUser.heroAvatar = heroAvatar;
        newUser.currHp=100;
        UserManager.addUser(newUser);



        // 将用户Id 保存至session, 用户发送自己位置时,需要用到id,id只在用户登录时还会带来
        ctx.channel().attr(AttributeKey.valueOf("userID")).set(userID);


        /**
         * 把消息转发出去
         * 使用buider 构建result
         */

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userID)
                .setHeroAvatar(heroAvatar);

        /**构建消息结果并广播
         *
         */

        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
