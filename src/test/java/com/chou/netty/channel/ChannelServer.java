package com.chou.netty.channel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author Chou
 * @Description
 * @ClassName ChannelServer
 * @Date 2023/7/9 22:46
 * @Version 1.0
 **/
@Slf4j
public class ChannelServer {
    public static void main(String[] args) {
        log.info("server begin start .... ");
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = msg instanceof ByteBuf ? (ByteBuf) msg :null;
                                log.info("-----{}", buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8000);
        log.info("server start end ....");
    }
}
