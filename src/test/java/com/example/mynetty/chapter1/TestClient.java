package com.example.mynetty.chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        sc.write(Charset.defaultCharset().encode("121344443454sfsdfsf"));
        System.in.read();
    }
}

