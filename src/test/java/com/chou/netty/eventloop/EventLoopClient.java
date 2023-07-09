package com.chou.netty.eventloop;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @Author Chou
 * @Description
 * @ClassName NettyClient
 * @Date 2023/5/29 20:35
 * @Version 1.0
 **/
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        // 启动器
        Channel channel = new Bootstrap()
                // 添加eventLoop
                .group(new NioEventLoopGroup())
                // 选择客户端channel 的是吸纳
                .channel(NioSocketChannel.class)
                // 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 在建立连接后被调用
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        nsc.pipeline().addLast(new StringEncoder());
                    }
                })
                // 连接到服务器
                .connect(new InetSocketAddress("localhost", 8000))
                .sync()
                .channel();
        channel.writeAndFlush("ok");
        System.in.read();

    }
}
