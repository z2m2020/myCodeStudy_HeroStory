package com.z2m2020.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import com.z2m2020.herostory.async.AsyncOperationProcessor;
import com.z2m2020.herostory.async.IAsyncOperation;
import com.z2m2020.herostory.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class RankService {
    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    /**
     * 单例
     */
    static private RankService _instance = new RankService();

    private RankService() {
    }

    public static RankService getInstance() {
        return _instance;
    }
    // 因为异步操作

    /**
     * 获取排行榜
     *
     * @param callback 回调函数
     */
    public void getRank(Function<List<RankItem>, Void> callback) {
        if (null == callback) {
            return;
        }
        AsyncOperationProcessor.getInstance().process(new AsyncGetRank() {


            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        });
    }

    public void refreshRank(int winnerId, int loserId) {
        if (winnerId <= 0 || loserId <= 0) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            redis.hincrBy("User_"+winnerId,"Win",1);
            redis.hincrBy("User_"+loserId,"Lose",1);

            String winStr=redis.hget("User_"+winnerId,"Win");
            int winNum=Integer.parseInt(winStr);

            redis.zadd("Rank",winNum,String.valueOf(winnerId));
        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }
    }

    /**
     * 异步方式获取排行榜
     */
    static private class AsyncGetRank implements IAsyncOperation {

        /**
         * 排名条目列表
         */
        private List<RankItem> _rankItemList;

        /**
         * 获取排名条目列表
         *
         * @return
         */
        List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {

            try (Jedis redis = RedisUtil.getJedis()) {

                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);
                int i = 0;
                List<RankItem> rankItemList = new ArrayList<>();

                for (Tuple t :
                        valSet) {
                    if (null == t) {
                        continue;
                    }
                    //获取用户Id
                    int userId = Integer.parseInt(t.getElement());
                    //获取用户信息
                    final String jsonStr = redis.hget("USer_" + userId, "BasicInfo");

                    if (null == jsonStr) {
                        continue;
                    }
                    RankItem newItem = new RankItem();
                    newItem.rankId = ++i;
                    newItem.userId = userId;
                    newItem.win = (int) t.getScore();

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvatar");

                    rankItemList.add(newItem);
                }

                _rankItemList = rankItemList;
            } catch (Exception ex) {
                //记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }

        }
    }
}
