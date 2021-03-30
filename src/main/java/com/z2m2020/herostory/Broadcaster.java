package com.z2m2020.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public final class Broadcaster {
    /**
     * 信道组,一定要使用static 不然无法群发
     *
     */

    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化构造器
     */
    private Broadcaster(){

    }

    /**
     * 添加信道
     * @param ch
     */
    static public void addChannel(Channel ch){
        if(null!=ch){
            _channelGroup.add(ch);
        }
    }

    /**
     * 移除信道
     */
    static public void removeChannel(Channel ch){
        if(null!=ch){
            _channelGroup.remove(ch);
        }
    }

    /**
     * 广播消息
     */
    static public void broadcast(Object msg){
        if (null != msg) {
            _channelGroup.writeAndFlush(msg);
        }
    }
}
