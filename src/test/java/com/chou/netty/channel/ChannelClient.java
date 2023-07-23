package com.chou.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Chou
 * @Description
 * @ClassName ChannelClient
 * @Date 2023/7/9 22:46
 * @Version 1.0
 **/
@Slf4j
public class ChannelClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 编码器
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // connect() 方法连接到服务器端、是一个异步非阻塞的方法
                .connect("127.0.0.1", 8000);
        //1. sync() 方法会阻塞connect 方法，连接成功之后才能继续往下走
        //channelFuture.sync();
        //Channel channel = channelFuture.channel();
        //log.info("channel info：{}", channel);
        //channel.writeAndFlush("hello!");
        //2.使用 addListener() 方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            // 需要等待与服务端建立好连接后才会开始调用operationComplete() 方法
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.info("channel info：{}", channel);
                channel.writeAndFlush("hello!");
            }
        });

    }
}
