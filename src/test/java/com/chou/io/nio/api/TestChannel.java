package com.chou.io.nio.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * 每个 Channel 对应一个 Buffer。
 * Selector 对应一个线程，一个线程对应多个 Channel。
 * 程序切换到那个 Channel 是由事件决定的（Event）。
 * Selector 会根据不同的事件，在各个通道上切换。
 * Buffer 就是一个内存块，底层是有一个数组。
 * 数据的读取和写入是通过 Buffer，但是需要flip()切换读写模式，而 BIO 是单向的，要么输入流要么输出流。
 *
 * @Author Chou
 * @Description Channel 通道可以理解为一个连接，这个连接可以连接到I/O 设备（磁盘文件，Socket），或者一个支持I/O访问的应用程序
 * @ClassName TestChannel
 * @Date 2023/5/27 22:58
 * @Version 1.0
 **/
public class TestChannel {
    public static void main(String[] args) throws IOException {
        //m1();
        //m2();
        FileInputStream fileInputStream = new FileInputStream("E:\\jast\\learn_netty\\src\\main\\resources\\b.txt");
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileChannel fileChannel = fileInputStream.getChannel();
        if (fileChannel.read(buffer) != -1){
            // 将buffer 切换到读的模式
            buffer.flip();
            System.out.println(Charset.defaultCharset().newDecoder().decode(buffer));
        }

    }

    private static void m2() {
        String path = ClassLoader.getSystemClassLoader().getResource("b.txt").getPath();
        System.out.println(path);
    }

    private static void m1() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("b.txt");
        int len = 0;
        byte bytes[] = new byte[1024];
        while ((len = inputStream.read(bytes)) != -1){
            System.out.println(new String(bytes,0,len));
        }
    }

}
