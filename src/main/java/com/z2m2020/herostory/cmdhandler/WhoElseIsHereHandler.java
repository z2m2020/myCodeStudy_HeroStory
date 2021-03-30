package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.User;
import com.z2m2020.herostory.UserManager;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WhoElseIsHereHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if(null==ctx||null==cmd){
            return;
        }

        final Predicate<User> userNoNull = u -> null != u;
        final BiConsumer<Integer, String> writeAndFlushUserInfo = (userId, heroAvatar) -> {
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(userId)
                    .setHeroAvatar(heroAvatar);

            ctx.writeAndFlush(resultBuilder.addUserInfo(userInfoBuilder).build());
        };
//        final Stream<Map<Integer, User>> userMap = Stream.of(_userMap);
//        final Collector<User, ?, Map<Integer, String>> userMapCollector = ;
        UserManager.listUser().stream().//Users
                filter(userNoNull).
                collect(Collectors.toMap(User::getUserId, User::getHeroAvatar)).
                forEach(writeAndFlushUserInfo);
    }

}