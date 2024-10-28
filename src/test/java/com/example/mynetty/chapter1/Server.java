package com.example.mynetty.chapter1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * @Author jwq
 * @Date 2023/3/11 23:37
 * 服务器
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //使用nio来理解阻塞模式,单线程
        //0.Bytebuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 改为非阻塞模式
        ssc.configureBlocking(false);
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3.连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();

        while (true) {
            // log.debug("connecting...");
            //4.accept建立与客户端的连接，SocketChannel用来与客户端之间通信
            SocketChannel sc = ssc.accept();// 默认阻塞的 阻塞意味着线程停止运行， 非阻塞模式下，线程会继续运行，如果没有建立连接，sc就是null
            if (sc != null) {
                log.debug("connected...{}", sc);
                sc.configureBlocking(false);// 设置为非阻塞模式
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // log.debug("before read...{}", channel);
                //5.接受客户端发送的数据
                int read = channel.read(buffer);//默认阻塞的 线程也会停止运行，设置为非阻塞，线程不会停，未读到数据，返回0
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }
            }
        }

    }
}
