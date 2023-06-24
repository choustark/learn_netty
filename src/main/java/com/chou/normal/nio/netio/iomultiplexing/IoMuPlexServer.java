package com.chou.normal.nio.netio.iomultiplexing;

import com.chou.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author Chou
 * @Description java nio 实现了io复用机制，可以使用nio的selector api 进行优化
 * @ClassName Server
 * @Date 2023/6/11 23:20
 * @Version 1.0
 **/
@Slf4j
public class IoMuPlexServer {
    public static void main(String[] args) throws IOException {
        // 创建一个selector 来管理多个 channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8000));
        // channel 与 selector 建立联系。将channel 注册到 selector中
        // 注册之后会返回一个selectionKey 用于监听selector和channel 之间的事件
        SelectionKey selectionKey = ssc.register(selector, 0, null);
        // 设置key 关注的事件
        // 场件的事件有一下几种：
        // accept 会有连接请求时触发
        // connect 是客户端，连接建立后触发
        // read 可读事件
        // write 可写事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key ... {}", selectionKey);
        while (true) {
            // select 没有事件 是阻塞的，有事件才会恢复运行
            // select 在时间处理事，让不会阻塞，事件发生后需要处理或者调用cancel() 方法取消，不能置之不理。
            selector.select();
            // 获取所有的可用时间并返回一个集合（包含了所有发生的事件）
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                log.debug("key {}", key);
                // 区分事件类型
                if (key.isAcceptable()) {
                    // 如果是 accept 接受事件
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // 接收连接的socketChannel
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    // 将接收的socket通道注册到selector中
                    SelectionKey scKey = sc.register(selector, 0, null);
                    // 并设置为可读事件
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                } else if (key.isReadable()) {
                    //如果是 read 可读事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        int read = channel.read(buffer);
                        if (read == -1) {
                            // 处理客户端正常断开后 read 事件完成，读取完数据cancel 事件
                            key.cancel();
                            channel.close();
                        } else {
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }
                    } catch (IOException e) {
                        //防止客户端非正常断开发生读事件导致客户端报错（水平触发）
                        e.printStackTrace();
                        key.cancel();
                    }

                }
                // 取消监听的key事件，nio 采用的是水平触发当事件状态没有改变时，select处于非阻塞状态
                //key.cancel();
                // 处理key 是需要将key 从selectedKeys 集合中删除，否则下次处理会出问题
                iterator.remove();
            }
        }
    }
}
