package com.chou.netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author Chou
 * @Description
 * @ClassName IoTest
 * @Date 2023/7/9 21:51
 * @Version 1.0
 **/
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        // 拆分group的分工2
        DefaultEventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                // 拆分group的分工1
                // 前面group 用于处理 NioServerSocketChannel 的accept() 事件
                // 第二个 group 处理 NioSocketChannel read()/write() 事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = msg instanceof ByteBuf ? (ByteBuf) msg :null;
                                log.info("-----{}", buf.toString(Charset.defaultCharset()));
                                ctx.fireChannelRead(msg);
                            }
                            // 添加第二个处理器，区分了group 分组，交给不同的group中的线程处理
                        }).addLast(group,"handler2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = msg instanceof ByteBuf ? (ByteBuf) msg :null;
                                log.info("-----{}", buf.toString(Charset.defaultCharset()));
                            }
                        });

                    }
                })
                .bind(8000);
    }
}
