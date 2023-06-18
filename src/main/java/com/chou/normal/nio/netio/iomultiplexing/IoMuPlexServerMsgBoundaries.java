package com.chou.normal.nio.netio.iomultiplexing;

import com.chou.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static com.chou.utils.ByteBufferUtil.debugAll;

/**
 * @Author Chou
 * @Description 处理消息边界问题（消息长度和服务设置byteBuffer 设置的长度不匹配 产生粘包/半包问题），采用http 的 ltv 的方式，处理消息边界问题
 *
 * @ClassName IoMuPlexServerMsgBoundaries
 * @Date 2023/6/13 23:05
 * @Version 1.0
 **/
@Slf4j
public class IoMuPlexServerMsgBoundaries {
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
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    // 给每个socket中的事件设置一个独有的ByteBuffer,处理当前channel 的数据
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                } else if (key.isReadable()) {
                    //如果是 read 可读事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            // 处理客户端正常断开后 read 事件完成，读取完数据cancel 事件
                            key.cancel();
                            channel.close();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
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

    /**
     * 解决读取消息产生粘包问题
     * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
     * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
     * - Hello,world\n
     * - I'm zhangsan\n
     * - How are you?\n
     * 变成了下面的两个 byteBuffer (黏包，半包)
     * - Hello,world\nI'm zhangsan\nHo
     * - w are you?\n
     * @param source
     */
    public static void split(ByteBuffer source){
        // 切换成读模式
        source.flip();
        // 获取bytebuffer的读取限制limit,
        int oldLimit = source.limit();
        // 循环读取限制
        for (int i = 0; i < oldLimit; i++) {
            // 判断\n 位置
            if (source.get(i) == '\n') {
                //如果当前读到了\n位置把读取数据存入新建一个新的bytebuffer
                // 新byteBuffer 计算新的容量，
                ByteBuffer target = ByteBuffer.allocate(i + 1 - source.position());
                // 获取通过limit 获取新的byteBuffer
                ByteBuffer saveBuffer = (ByteBuffer) source.limit(i + 1);
                target.put(saveBuffer);
                debugAll(target);
                // 重新设置有内容数据为原始数据
                source.limit(oldLimit);
            }
        }
        // 将未读取的数据压缩
        source.compact();
    }
}
