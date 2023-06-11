package com.chou.normal.nio.api;

import com.chou.utils.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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



    }

    private static void m3() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        buffer.flip();
        byte[] bytes = new byte[1];
        System.out.println("-----"+bytes.length);
        ByteBuffer buffer1 = buffer.get(bytes);
        System.out.println(StandardCharsets.UTF_8.decode(buffer1));
        ByteBufferUtil.debugAll(buffer);
        buffer.rewind();
        ByteBufferUtil.debugAll(buffer);
    }

    private static void m2() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 0x65);
        byteBuffer.put((byte) 0x64);
        byteBuffer.put((byte) 0x63);
        byteBuffer.put((byte) 0x66);
        byteBuffer.mark();
        System.out.println(byteBuffer.get(1));
        ByteBufferUtil.debugAll(byteBuffer);
    }

    private static void m1() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("hello nio".getBytes());
        byteBuffer.flip();
        ByteBufferUtil.debugAll(byteBuffer);
    }
}
