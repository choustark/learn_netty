package com.chou.netty.channel;

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
 * @Description 异步关闭客户端
 * @ClassName CloseFutureClient
 * @Date 2023/7/23 17:45
 * @Version 1.0
 **/
@Slf4j
public class CloseFutureClientNoThread {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        // 编码器
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8000);
        Channel channel = channelFuture.sync().channel();
        log.debug("channel info >>>>{}", channel);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            if ("q".equals(s)) {
                channel.close();
                log.debug("客户端已经关闭、开始处理关闭后的操作！");
//                System.exit(0);
                break;
            }
            channel.writeAndFlush(s);
        }
        // 通过closeFuture() 方法来处理关闭
        // 这里有两种处理关闭的方式
        //      a、调用sync() 方法来同步的处理关闭
        //      b、使用addListener() 方法异步来处理关闭
        /*ChannelFuture closeFuture = channel.closeFuture();
        log.debug("waiting for close....");
        closeFuture.sync();
        log.debug("客户端已经关闭、开始处理关闭后的操作！");

        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

            }
        });*/
    }


}
