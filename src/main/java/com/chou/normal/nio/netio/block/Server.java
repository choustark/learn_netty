package com.chou.normal.nio.netio.block;

import com.chou.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * @Author Chou
 * @Description TODO
 * @ClassName Server
 * @Date 2023/6/11 13:53
 * @Version 1.0
 **/
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ArrayList<SocketChannel> socketChannels = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(16);
        while (true) {
            log.debug("connecting ... ");
            // accept() 是个阻塞方法，如果没有客户端连接过来，会一直等待阻塞在这里
            // 可以使用configureBlocking() 来设置是否阻塞模式 默认false
            SocketChannel sc = ssc.accept();
            log.debug("connected ...{}",sc);
            socketChannels.add(sc);
            for (SocketChannel channel : socketChannels) {
                log.debug("before read ...");
                // read() 方法和accept() 方法同理，
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugAll(buffer);
                    buffer.clear();
                    log.debug("after read ...");
                }
            }
        }
    }
}
