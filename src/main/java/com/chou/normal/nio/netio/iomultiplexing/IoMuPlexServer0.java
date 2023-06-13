package com.chou.normal.nio.netio.iomultiplexing;

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
import java.util.Set;

/**
 * @Author Chou
 * @Description java nio 实现了io复用机制，可以使用nio的selector api 进行优化
 * @ClassName Server
 * @Date 2023/6/11 23:20
 * @Version 1.0
 **/
@Slf4j
public class IoMuPlexServer0 {
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
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel sc = channel.accept();
                sc.configureBlocking(false);
                log.debug("{}", sc);
                // 取消监听的key事件，nio 采用的是水平触发当事件状态没有改变时，select处于非阻塞状态
                //key.cancel();
            }
        }
    }
}
