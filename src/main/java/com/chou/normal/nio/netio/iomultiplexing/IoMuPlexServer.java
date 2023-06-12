package com.chou.normal.nio.netio.iomultiplexing;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

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
        ssc.bind(new InetSocketAddress(8000));
        // channel 与 selector 建立联系。将channel 注册到 selector中
        // 注册之后会返回一个selectionKey 用于监听selector和channel 之间的事件
        SelectionKey selectionKey = ssc.register(selector, 0, null);
        // 设置key 关注的事件
        // 场件的事件有一下几种：
        //
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key ... {}",selectionKey);

    }
}
