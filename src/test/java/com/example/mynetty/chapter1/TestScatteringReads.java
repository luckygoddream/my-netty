package com.example.mynetty.chapter1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author jwq
 * @Date 2023/3/11 14:47
 * 分散读取
 */
public class TestScatteringReads {
    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            ByteBuffer b1 = ByteBuffer.allocate(3);//设置每次读取的长度 读3个
            ByteBuffer b2 = ByteBuffer.allocate(3);//设置每次读取的长度 读3个
            ByteBuffer b3 = ByteBuffer.allocate(5);//设置每次读取的长度 读5个
            channel.read(new ByteBuffer[]{b1,b2,b3});
            b1.flip();
            b2.flip();
            b3.flip();
            ByteBufferUtil.debugAll(b1);
            ByteBufferUtil.debugAll(b2);
            ByteBufferUtil.debugAll(b3);
        } catch (IOException e) {
        };
    }
}
