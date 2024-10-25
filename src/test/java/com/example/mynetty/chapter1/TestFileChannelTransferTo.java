package com.example.mynetty.chapter1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @Author jwq
 * @Date 2023/3/11 15:44
 * 传输数据从from到to
 */
public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try {
            FileChannel from = new FileInputStream("data.txt").getChannel();
            FileChannel to = new FileOutputStream("to.txt").getChannel();
            //效率高，底层操作系统的零拷贝优化，数据上限一次最多2g
            long size = from.size();
            //left剩余多少没有传输
            for (long left = size; left > 0; ) {
                System.out.println("position:" + (size - left) + " left:" + left);
                left -= from.transferTo((size - left), left, to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
