package com.chou.netty.hw.server;

import com.sun.corba.se.spi.activation.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @Author Chou
 * @Description 使用netty 写客户端
 * @ClassName NettyServer
 * @Date 2023/5/21 21:59
 * @Version 1.0
 **/
public class NettyServer {
    public static void main(String[] args) {
        // 服务器端启动器
        new ServerBootstrap()
                // group 组
                .group(new NioEventLoopGroup())
                // 选择服务器的serverSocketChannel的实现
                .channel(NioServerSocketChannel.class)
                // 负责group中的处理连接，对连接给出相应的handler
                .childHandler(
                        // channel 代表和客户端进行数据读写的通道初始化。负责添加别的handle
                        new ChannelInitializer<NioSocketChannel>() {
                            // 添加具体的handler
                            @Override
                            protected void initChannel(NioSocketChannel nsc) throws Exception {
                                nsc.pipeline().addLast(new StringDecoder()); // 将bytebuffer 转字符串
                                nsc.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义的handler
                                    // 读事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        //打印转换好的字符串
                                        System.out.println(msg);
                                    }
                                });

                            }
                            // 绑定端口号
                        }).bind(8000);
    }
}
