package com.chou.normal.nio.netio.iomultiplexing.concurrent;

import com.chou.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Author Chou
 * @Description 分离accept 和 read write事件,多线程模式雏形
 * @ClassName MultiThreadServer
 * @Date 2023/6/23 0:33
 * @Version 1.0
 **/
@Slf4j
public class MultiThreadServerV0 {
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
        work.init();
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
                    // channel 关联selector
                    channel.register(work.work, SelectionKey.OP_READ, null);
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
        public void init() throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                work = Selector.open();
                thread.start();
                start = true;
            }
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
