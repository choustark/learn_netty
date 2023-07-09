package com.chou.io.nio.netio.iomultiplexing.concurrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author Chou
 * @Description TODO
 * @ClassName MultiTreadClinet
 * @Date 2023/6/23 0:33
 * @Version 1.0
 **/
public class MultiTreadClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8000));
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("123456789abcdef".getBytes());
        //sc.write(Charset.defaultCharset().encode("123456789abcdef"));
        buffer.flip();
        sc.write(buffer);
        System.in.read();

    }

}
