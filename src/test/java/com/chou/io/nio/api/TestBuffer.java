package com.chou.io.nio.api;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.chou.utils.ByteBufferUtil.debugAll;

/**
 * @Author Chou
 * @Description 缓存区 Buffer
 * @ClassName TestBuffer
 * @Date 2023/5/27 23:06
 * @Version 1.0
 **/
public class TestBuffer {
    public static void main(String[] args) {
        //m1();
        //m2();
        //m3();
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.put("Hello,world\nI'm zhangsan\nH".getBytes());
        split(buffer);
        buffer.put("ow are you?\n".getBytes());
        split(buffer);


    }

    public static void split(ByteBuffer source) {

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

    private static void m3() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        byte[] bytes = new byte[1];
        System.out.println("-----" + bytes.length);
        ByteBuffer buffer1 = buffer.get(bytes);
        System.out.println(StandardCharsets.UTF_8.decode(buffer1));
        debugAll(buffer);
        buffer.rewind();
        debugAll(buffer);
    }

    private static void m2() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 0x65);
        byteBuffer.put((byte) 0x64);
        byteBuffer.put((byte) 0x63);
        byteBuffer.put((byte) 0x66);
        byteBuffer.mark();
        System.out.println(byteBuffer.get(1));
        debugAll(byteBuffer);
    }

    private static void m1() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("hello nio".getBytes());

        byteBuffer.flip();
        debugAll(byteBuffer);
    }
}
