package com.z2m2020.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import com.z2m2020.herostory.rank.RankService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MqConsumer {
    static private Logger LOGGER = LoggerFactory.getLogger(MqConsumer.class);


    private MqConsumer(){}

    static public void inti(){
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr("192.168.64.200:9876");

        try{
            consumer.subscribe("herostory_victor","*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExtlist, ConsumeConcurrentlyContext ctx) {

                    for(MessageExt msgExt:msgExtlist){
                        VictorMsg victorMsg = JSONObject.parseObject(
                                msgExt.getBody(), VictorMsg.class);

                        LOGGER.info("从消息队列中收到胜利消息,winnerId={},loserId={}",victorMsg.winnerId,victorMsg.loserId);

                        RankService.getInstance().refreshRank(victorMsg.winnerId,victorMsg.loserId);
                    };

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();

            LOGGER.info("consumer消息队列连接成功");
        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }
    }

}
