package com.chou.netty.pipeline;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @Author Chou
 * @Description pipeline 测试客户端
 * @ClassName TestPipelineClient
 * @Date 2023/7/23 22:55
 * @Version 1.0
 **/
@Slf4j
public class TestPipelineClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect("localhost", 8000);
        Channel channel = channelFuture.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String s = scanner.next();
                if ("q".equals(s)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(s);
            }
        }, "input_work_1").start();



        ChannelFuture closeFuture = channel.closeFuture();
        log.debug("客户端在等待关闭======");
        /*closeFuture.sync();
        group.shutdownGracefully();
        log.debug("客户端关闭了======");*/
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("客户端关闭了======");
                group.shutdownGracefully();
            }
        });
    }
}
