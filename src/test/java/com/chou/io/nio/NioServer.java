package com.chou.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author Chou
 * @Description 使用nio api 写服务
 * @ClassName NioServer
 * @Date 2023/5/25 23:21
 * @Version 1.0
 **/
public class NioServer {
    public static void main(String[] args) throws IOException {
        Selector serverSelector = Selector.open();
        Selector clientSelector = Selector.open();
        new Thread(() -> {
            try {
                // 是一个可以监听新进来tcp 连接的通道，就像标准IO中的ServerSocket 一样
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(8000));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
                while (true) {
                    // 监听是否有新的连接。这里参数 1 是指阻塞的时间为 1ms
                    if (serverSelector.select(1) > 0) {
                        Set<SelectionKey> keySet = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = keySet.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isAcceptable()) {
                                try {
                                    // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    keyIterator.remove();
                                }

                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    // 批量轮询是否有那些连接有数据可读，这里的1 是指阻塞时间为1 ms
                    if (clientSelector.select(1) > 0) {
                        Set<SelectionKey> keySet = serverSelector.selectedKeys();
                        Iterator<SelectionKey> iterator = keySet.iterator();
                        while (iterator.hasNext()){
                            SelectionKey key = iterator.next();
                            if(key.isReadable()){
                                try{
                                    SocketChannel socketChannel = (SocketChannel) key.channel();
                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                                    // nio 面向buffer
                                    socketChannel.read(buffer);
                                    buffer.flip();
                                    System.out.println(Charset.defaultCharset().newDecoder().decode(buffer).toString());
                                }finally {
                                    iterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }).start();

    }
}
