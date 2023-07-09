package com.chou.io.nio.netio.unblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @Author Chou
 * @Description TODO
 * @ClassName Client
 * @Date 2023/6/11 20:41
 * @Version 1.0
 **/
public class UnBlockClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1",8080));
        System.out.println("waiting ...");
    }
}
