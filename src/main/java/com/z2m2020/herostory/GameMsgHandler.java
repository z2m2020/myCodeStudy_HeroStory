package com.z2m2020.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.sun.org.glassfish.gmbal.GmbalException;
import com.z2m2020.herostory.cmdhandler.*;
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


    /**
     * 用户字典
     *
     * @param ctx
     * @throws Exception
     */

//    static private final Map<Integer, User> _userMap = new HashMap<>();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);
            Broadcaster.addChannel(ctx.channel());

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }


    }

    /**
     * 拿到用户id,把用从_userMap中移除
     *
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }
        try {
            super.handlerRemoved(ctx);

            Integer userID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();

            if (null == userID) {
                return;
            }

            UserManager.removeByUserId(userID);
            Broadcaster.removeChannel(ctx.channel());

            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userID);

            final GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
            Broadcaster.broadcast(newResult);


        } catch (Exception ex) {
            //记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("收到客户端消息,msgClazz={} ,msg={} ",
                msg.getClass().getSimpleName(),
                msg
        );


        /**
         *  得到其他玩家发来的用户信息,并转发
         */

        try {
            ICmdHandler<? extends GeneratedMessageV3> cmdHandler= CmdHandlerFactory.create(msg.getClass());
            if(null!=cmdHandler){
                cmdHandler.handle(ctx,this.cast(msg));
            }
        }catch (Exception ex){
            //记录错误日志
            LOGGER.error(ex.getMessage(),ex);
        }
    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg){
        if(null==msg){
            return null;

        }else{
            return (TCmd) msg;
        }
    }

}
