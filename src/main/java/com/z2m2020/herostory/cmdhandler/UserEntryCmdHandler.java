package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.Broadcaster;
import com.z2m2020.herostory.model.User;
import com.z2m2020.herostory.model.UserManager;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    static private final Logger LOGGER = LoggerFactory.getLogger(UserEntryCmdHandler.class);



    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd){
        LOGGER.info("创建角色中");

        if(null==ctx|| null==cmd){
            return;
        }
        Integer userId =(Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();

        if(null==userId){
            return;
        }

        User existUser=UserManager.getByUserId(userId);


        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();

        resultBuilder
                .setUserId(userId)
                .setUserName(existUser.userName)
                .setHeroAvatar(existUser.heroAvatar);

        /**构建消息结果并广播
         *
         */
        LOGGER.info("角色创建完毕");

        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
