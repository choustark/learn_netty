package com.chou.netty.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author Chou
 * @Description
 * @ClassName Test1
 * @Date 2023/7/8 23:51
 * @Version 1.0
 **/
@Slf4j(topic = "test1")
public class Test1 {
    public static void main(String[] args) throws IOException {
        System.out.println(NettyRuntime.availableProcessors());
        // 创建事件组
        EventLoopGroup group = new NioEventLoopGroup(2);
        // 获取下一个循环对象
        System.out.println(group.next());
        // 执行普通任务
        //normal(group);
        // 执行一个定时任务
        schedule1(group);
        //schedule2(group);

    }

    private static void schedule2(EventLoopGroup group) {
        group.next().scheduleAtFixedRate(() -> {
            log.debug("这是一个定时任务");
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static void schedule1(EventLoopGroup group) throws IOException {
        group.next().schedule(() -> {
            log.debug("这是一个定时任务");
        }, 60, TimeUnit.SECONDS);
        System.in.read();
    }

    private static void normal(EventLoopGroup group) {
        group.next().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("ok");
        });
        log.debug("main");
    }
}
