package com.z2m2020.herostory;

import com.sun.org.glassfish.gmbal.GmbalException;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken.Optional;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
     * 用户字典
     *
     * @param ctx
     * @throws Exception
     */

    static private final Map<Integer, User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);
            _channelGroup.add(ctx.channel());

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }


    }

    /**
     * 拿到用户id,把用从_userMap中移除
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)  {

       try{
           super.handlerRemoved(ctx);

           Integer userID=(Integer)ctx.channel().attr(AttributeKey.valueOf("userID")).get();

           if(null==userID){
               return;
           }

           _userMap.remove(userID);

           GameMsgProtocol.UserQuitResult.Builder resultBuilder=GameMsgProtocol.UserQuitResult.newBuilder();
           resultBuilder.setQuitUserId(userID);

           final GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
           _channelGroup.writeAndFlush(newResult);
       }catch(Exception ex){
           //记录错误日志
           LOGGER.error(ex.getMessage(),ex);
       }
    }

    /**
     * 信道组
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("收到客户端消息,msgClazz={} ,msg={} ",
                msg.getClass().getSimpleName(),
                msg
        );


        /**
         *  得到其他玩家发来的用户信息,并转发
         */

        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userID = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            /**
             * 把新入场的用户存起来
             */

            User newUser = new User();
            newUser.userId = userID;
            newUser.heroAvatar = heroAvatar;
            _userMap.putIfAbsent(userID, newUser);



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
            _channelGroup.writeAndFlush(newResult);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
//            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            /**
             *  写出所有用户
             */
            //===============================================
//            for(User currUser: _userMap.values()){
//                if(null==currUser){
//                    continue;
//                }
//
//                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder=GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
//                userInfoBuilder.setUserId(currUser.userId)
//                        .setHeroAvatar(currUser.heroAvatar);
//                resultBuilder.addUserInfo(userInfoBuilder);
//
//                GameMsgProtocol.WhoElseIsHereResult newResult=resultBuilder.build();
//                ctx.writeAndFlush(newResult);

            //================================================
//                        Function f =
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
            _userMap.values().stream().//Users
                    filter(userNoNull).
                    collect(Collectors.toMap(User::getUserId, User::getHeroAvatar)).
                    forEach(writeAndFlushUserInfo);
        }else if (msg instanceof GameMsgProtocol.UserMoveToCmd){
            //
            //用户移动
            //

            //从session取回userID
            Integer UserId=(Integer)ctx.channel().attr(AttributeKey.valueOf("userID")).get();

            //非空判断,避免服务器崩掉
            if(null==UserId){
                return;
            }

            GameMsgProtocol.UserMoveToCmd cmd=(GameMsgProtocol.UserMoveToCmd)msg;
            GameMsgProtocol.UserMoveToResult.Builder resultBuilder=GameMsgProtocol.UserMoveToResult.newBuilder();


            //用户id从哪里获取?

            //从session中拿id
            // --在用户入场的时候,把它的id赛到与之关联的信道中去
            resultBuilder.setMoveUserId(UserId);

            resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
            //消息封装好了,继续添加decoder和encoder
            final GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);

        }



    }
}

