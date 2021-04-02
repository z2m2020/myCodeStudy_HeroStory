package com.z2m2020.herostory;

import com.z2m2020.herostory.mq.MqConsumer;
import com.z2m2020.herostory.util.RedisUtil;

public class RankApp {
    static public void main(String[] Args){
        RedisUtil.init();
        MqConsumer.inti();
    }
}
