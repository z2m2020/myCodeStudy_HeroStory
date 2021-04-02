package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.Broadcaster;
import com.z2m2020.herostory.model.User;
import com.z2m2020.herostory.model.UserManager;
import com.z2m2020.herostory.mq.MqProducer;
import com.z2m2020.herostory.mq.VictorMsg;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户攻击处理器
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd userAttkCmd) {
        LOGGER.info("UserAtta");

        //获取攻击用户的id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();

        if (null == attkUserId) {
            return;
        }

        //获取目标用户Id
        Integer targetUserId = userAttkCmd.getTargetUserId();

        //获取目标用户
        final User targetUser = UserManager.getByUserId(targetUserId);

        if (null == targetUser) {
            broadcastAttkResult(attkUserId,-1);

            return;
        }

        LOGGER.info("当前线程= {}",Thread.currentThread().getName());
        final int dmgPoint=10;
        targetUser.currHp=targetUser.currHp-dmgPoint;

        //广播攻击结果
        broadcastAttkResult(attkUserId,targetUserId);

        //广播减血结果
        broadcastSubtractHpResult(attkUserId,dmgPoint);

        //广播对手死亡结果
        if(0>=targetUser.currHp){
            broadcastDieResult(targetUserId);

            VictorMsg newMsg=new VictorMsg();
            newMsg.winnerId=attkUserId;
            newMsg.loserId=targetUserId;
            MqProducer.sendMsg("herostory_victor",newMsg);

        }

    }

    /**
     * 广播攻击结果
     * @param attkUserId 主角id
     * @param targetUserId 敌人id
     */
    static private  void broadcastAttkResult(int attkUserId,int targetUserId){
        if(attkUserId<0){return;}

        final GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        final GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

    }

    /**
     * 广播减血结果
     * @param targetUserId 敌人id
     * @param subtractHp 减少的血量
     */
    static private void broadcastSubtractHpResult(int targetUserId,int subtractHp){
        if (targetUserId<=0|| subtractHp<=0){
            return;
        }

        final GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();

        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        final GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();

        Broadcaster.broadcast(newResult);


    }

    /**
     * 广播角色死亡消息
     * @param targetUserId 角色id
     */
    static private void broadcastDieResult(int targetUserId){
        final GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();

        resultBuilder.setTargetUserId(targetUserId);


        final GameMsgProtocol.UserDieResult newResult = resultBuilder.build();

        Broadcaster.broadcast(newResult);
    }
}
