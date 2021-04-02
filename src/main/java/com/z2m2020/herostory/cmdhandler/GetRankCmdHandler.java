package com.z2m2020.herostory.cmdhandler;

import com.z2m2020.herostory.msg.GameMsgProtocol;
import com.z2m2020.herostory.rank.RankItem;
import com.z2m2020.herostory.rank.RankService;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;

public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if(null==ctx||null==cmd){
            return;
        }

        RankService.getInstance().getRank((rankItemList)->{
            if(null==rankItemList){
                rankItemList= Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder=GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem rankItem: rankItemList){
                if(null==rankItem){
                    continue;
                }
                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder=GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                rankItemBuilder.setRankId(rankItem.rankId)
                        .setUserId(rankItem.userId)
                        .setUserName(rankItem.userName)
                        .setHeroAvatar(rankItem.heroAvatar)
                        .setWin(rankItem.win);

                resultBuilder.addRankItem(rankItemBuilder);
            }

            GameMsgProtocol.GetRankResult newResult=resultBuilder.build();
            ctx.writeAndFlush(newResult);

            return null;
        });


    }
}
