package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.login.LoginService;
import com.z2m2020.herostory.login.db.UserEntity;
import com.z2m2020.herostory.model.User;
import com.z2m2020.herostory.model.UserManager;
import com.z2m2020.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 用户登录
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (null == userName || null == password) {
            return;
        }

        //获取用户实体
        UserEntity userEntity = LoginService.getInstance().userLogin(userName, password);

        GameMsgProtocol.UserLoginResult.Builder resultBuilder=GameMsgProtocol.UserLoginResult.newBuilder();



        if(null!=userEntity){
            resultBuilder.setUserId(-1);
            resultBuilder.setUserName("");
            resultBuilder.setHeroAvatar("");


        }else {
//            int userID = -1;//cmd.getUserId();
//            String heroAvatar =""; // cmd.getHeroAvatar();
//
            //用户入场消息
            //
            User newUser = new User();
            newUser.userId = userEntity.userId;
            newUser.userName=userEntity.userName;
            newUser.heroAvatar = userEntity.heroAvatar;
            newUser.currHp=100;
            UserManager.addUser(newUser);



            // 将用户Id 保存至session, 用户发送自己位置时,需要用到id,id只在用户登录时还会带来
            ctx.channel().attr(AttributeKey.valueOf("userID")).set(newUser.userId);
            resultBuilder.setUserId(userEntity.userId);
            resultBuilder.setUserName(userEntity.userName);
            resultBuilder.setHeroAvatar(userEntity.heroAvatar);
        }

        GameMsgProtocol.UserLoginResult newResult=resultBuilder.build();
        ctx.writeAndFlush(newResult);


    }
}
