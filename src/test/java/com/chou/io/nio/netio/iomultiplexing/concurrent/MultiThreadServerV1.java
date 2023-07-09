package com.chou.io.nio.netio.iomultiplexing.concurrent;

import com.chou.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author Chou
 * @Description 解决select() 线程死锁问题
 * @ClassName MultiThreadServer
 * @Date 2023/6/23 0:33
 * @Version 1.0
 **/
@Slf4j
public class MultiThreadServerV1 {
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
        Work work = new Work("work-1");
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
                    work.init(channel);
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
        // 一个队列 用于存放channel 和 selector 的注册任务，用于解决select()阻塞被阻塞问题
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        // 初始化餐参数
        public void init(SelectableChannel channel) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                work = Selector.open();
                thread.start();
                start = true;
            }
            queue.add(() -> {
                try {
                    // channel 关联selector
                    channel.register(work, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            work.wakeup();

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
                    Runnable task = queue.poll();
                    if (null != task) {
                        task.run();
                    }
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
