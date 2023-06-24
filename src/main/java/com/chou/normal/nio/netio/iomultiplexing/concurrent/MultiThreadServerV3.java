package com.chou.normal.nio.netio.iomultiplexing.concurrent;

import com.chou.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Chou
 * @Description 解决select() 线程死锁问题  使用selector.wakeup 方法。多work(线程)工作方式
 * @ClassName MultiThreadServer
 * @Date 2023/6/23 0:33
 * @Version 1.0
 **/
@Slf4j
public class MultiThreadServerV3 {
    public static void main(String[] args) throws IOException {
        log.debug("----------------");
        // 创建severChannel服务
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Thread.currentThread().setName("boss");
        // 非阻塞，默认true 阻塞状态
        ssc.configureBlocking(false);
        // 创建selector
        Selector boss = Selector.open();
        // 将ssc 注册到selector 中 得到事件
        SelectionKey selectionKey = ssc.register(boss, 0, null);
        // 设置selector 监听的事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        // 给服务绑定端口
        ssc.bind(new InetSocketAddress(8000));
        int processors = Runtime.getRuntime().availableProcessors();
        Work[] works = new Work[processors];
        for (int i = 0; i < processors; i++) {
            works[i] = new Work("work-" + i);
        }
        AtomicInteger count = new AtomicInteger();
        // 循环处理事件
        while (true) {
            // 选择一个时间事件处理
            log.debug("begin deal event...");
            boss.select();
            Iterator<SelectionKey> keyIterator = boss.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                // 获取事件类型
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel sc = (ServerSocketChannel) key.channel();
                    SocketChannel channel = sc.accept();
                    channel.configureBlocking(false);
                    log.debug("connected ...{}", channel.getRemoteAddress());
                    log.debug("before register ...{}", channel.getRemoteAddress());
                    // 轮询的方式分配的不同的work线程
                    works[count.getAndIncrement() % works.length].init(channel);
                    log.debug("after register ...{}", channel.getRemoteAddress());

                }
            }
        }
    }

    // 创建处理除accept 事件的其他事件类型
    static class Work implements Runnable {
        private Thread thread;
        private String name;
        private Selector work;
        private volatile boolean start = false;

        // 初始化餐参数
        public void init(SelectableChannel channel) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                work = Selector.open();
                thread.start();
                start = true;
            }
            // 盲猜底层使用了park() 和 unPark() 方法来解决线程唤醒问题
            work.wakeup();
            channel.register(work, SelectionKey.OP_READ, null);

        }

        public Work(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    log.debug("work is working...");
                    work.select();
                    Iterator<SelectionKey> keyIterator = work.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel sc = (SocketChannel) key.channel();
                            log.debug("reed....{}", sc.getRemoteAddress());
                            sc.read(buffer);
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
