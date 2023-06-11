package com.chou.normal.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author Chou
 * @Description TODO
 * @ClassName IOServer
 * @Date 2023/5/21 22:07
 * @Version 1.0
 **/
public class IOServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        // 接受连接的线程
        new Thread(() -> {
            while (true) {
                try {
                    // accept() 是一个阻塞方法，不停的获取新连接
                    Socket socket = serverSocket.accept();
                    // 每一个socket 都需要一个线程来处理数据
                    new Thread(() -> {
                        try {
                            int len;
                            byte[] data = new byte[1024];
                            InputStream stream = socket.getInputStream();
                            while ((len = stream.read(data)) != -1) {
                                // 按字节流方式读取数据
                                System.out.println(Thread.currentThread().getName() + "---- " + new String(data, 0, len));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
