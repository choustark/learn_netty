package com.chou.normal.nio.netio.iomultiplexing;

import io.netty.channel.ServerChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @Author Chou
 * @Description 写事件处理
 * @ClassName WriteServer
 * @Date 2023/6/18 22:04
 * @Version 1.0
 **/
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector selector = Selector.open();
        ssc.configureBlocking(false);
        // 将channel 注册到selector
        SelectionKey selectionKey = ssc.register(selector, 0, null);
        ssc.bind(new InetSocketAddress(8000));
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel sc = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = sc.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey scKey = socketChannel.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    //给客户端发送消息
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        stringBuilder.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(stringBuilder.toString());
                    int write = socketChannel.write(buffer);
                    System.out.println(write);
                    if (buffer.hasRemaining()) {
                        // 缓存区中还有可写空间
                        // 给key在原来的基础上添加事件
                        scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        //并将buffer,带入到下一个事件
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    if (!buffer.hasRemaining()) {
                        // 清理
                        key.attach(null);
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }

                }
            }
        }
    }
}
