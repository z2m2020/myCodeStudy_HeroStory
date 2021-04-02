package com.z2m2020.herostory;

import com.z2m2020.herostory.model.UserManager;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            Broadcaster.removeChannel(ctx.channel());

            Integer userID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();

            if (null == userID) {
                return;
            }

            UserManager.removeByUserId(userID);


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
        if(null==ctx|| null==msg){
            return;
        }

        MainMsgProcessor.getInstance().process(ctx, msg);

    }
}
