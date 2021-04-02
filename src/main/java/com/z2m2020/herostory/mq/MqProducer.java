package com.z2m2020.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消息队列生产者
 */
public final class MqProducer {

    /**
     * 日志消息
     */
    static private final Logger LOGGER= LoggerFactory.getLogger(MqProducer.class);

    static private DefaultMQProducer _producer=null;

    /**
     * 私有化默认构造器
     */
    private MqProducer() {
    }

    static public void init(){
        try{

            final DefaultMQProducer producer = new DefaultMQProducer("herostory");
//            producer.setNamesrvAddr("192.168.64.200:9876");
            producer.setNamesrvAddr("192.168.64.200:9876");

            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer=producer;

            LOGGER.info("消息队列(生产者)连接成功");
        }catch (Exception ex ){
            LOGGER.error(ex.getMessage(),ex);
        }
    }
    static public void sendMsg(String topic,Object msg){
        if(null==topic|| null==msg){
            return;
        }

        Message newMsg=new Message();
        newMsg.setTopic(topic);
        newMsg.setBody(JSONObject.toJSONBytes(msg));

        try{
            _producer.send(newMsg);

        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }

    }
}
