package com.chou.normal.nio.netio.iomultiplexing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author Chou
 * @Description 客户端
 * @ClassName WriteClient
 * @Date 2023/6/18 22:05
 * @Version 1.0
 **/
public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8000));
        int count = 0;
        // 接受数据
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            count += socketChannel.read(buffer);
            System.out.println(count);
            buffer.clear();
        }
    }
}
