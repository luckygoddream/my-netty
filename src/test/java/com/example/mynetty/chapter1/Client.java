package com.example.mynetty.chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @Author jwq
 * @Date 2023/3/11 23:58
 * 客户端
 */
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8080));
        SocketAddress localAddress = sc.getLocalAddress();
        sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        // System.out.println("waiting...");
        System.in.read();
    }
}
