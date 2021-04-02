package com.z2m2020.herostory;

import com.z2m2020.herostory.cmdhandler.CmdHandlerFactory;
import com.z2m2020.herostory.mq.MqProducer;
import com.z2m2020.herostory.util.RedisUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.snmp.util.MibLogger;

public class ServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        /**
         * 配置日志
         */
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));


        //初始化 cmdHandlerFactory
        CmdHandlerFactory.init();

        //初始化 消息识别器
        GameMsgRecognizer.init();

        //初始化mysql会话工厂
        MySqlSessionFactory.init();

        //初始化Redis
        RedisUtil.init();
        //初始化消息队列
        MqProducer.init();

        //netty代码
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workerGroup =new NioEventLoopGroup();

        try{
            final ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(),
                                    new HttpObjectAggregator(65536),
                                    new WebSocketServerProtocolHandler("/websocket"),
                                    new GameMsgDecoder(),
                                    new GameMsgEncoder(),
                                    new GameMsgHandler()

                            );

                        }
                    });
            b.option(ChannelOption.SO_BACKLOG,128);
            b.childOption(ChannelOption.SO_KEEPALIVE,true);

            final ChannelFuture f = b.bind(12345).sync();
            
            if(f.isSuccess()){
                LOGGER.info("游戏服务器启动成功");
            }

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage(),e);
        }finally{
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
