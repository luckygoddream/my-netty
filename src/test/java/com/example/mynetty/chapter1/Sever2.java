package com.example.mynetty.chapter1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

@Slf4j
public class Sever2 {
    private static void  split(ByteBuffer buffer){
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            boolean b = buffer.get(i) == '\n';
            if (b){
                //判断完成数据长度
                int len = i + 1 - buffer.position();
                //完整消息存到新的Bytebuffer
                ByteBuffer target = ByteBuffer.allocate(len);
                //从buffer读，向target写
                for (int j = 0; j < len; j++) {
                    target.put(buffer.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        buffer.compact();
    }

    public static void main(String[] args) throws IOException {
        // 1创建selector,管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        // 2建立selector和channel的联系（注册）
        // SelectionKey就是将来事件发生后，通过它可以知道事件和那个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}", sscKey);
        //3.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 4 select方法，没有事件发生，线程阻塞，有事件 线程才会恢复运行
            // select在事件未处理时，它不会阻塞。事件发生后要么处理要么取消不能置之不理
            selector.select();
            // 5 处理事件  selectedKeys是个集合，包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 处理Key时，要从selectedKeys集合中删除，否则下次处理就会有问题（空指针）
                iterator.remove();
                log.debug("key:{}", key);
                // 区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    // 不能被多个channel共享 线程不安全因此需要为每个channel维护一个独立的ByteBuffer
                    ByteBuffer buffer = ByteBuffer.allocate(16);//attachment 附件
                    // 将一个ByteBuffer作为附件关联到SelectionKey上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("sc:{}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();// 拿到触发事件的channel
                        //获取SelectionKey上关联的附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);// 正常断开 返回值为-1
                        if (read == -1) {
                            key.cancel();
                        }else {
                            split(buffer);
                            if (buffer.limit() == buffer.position()) {
                                // 扩容
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                //替换SelectionKey上关联的附件（ByteBuffer）
                                key.attach(newBuffer);
                            }
                            // buffer.flip();
                            // ByteBufferUtil.debugRead(buffer);
                            // 消息边界
                            System.out.println(Charset.defaultCharset().decode(buffer));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();// 因为客户端断开了，因此需要将key取消（从selector的keys集合中真正 删除key）
                    }
                }

                // key.cancel();//取消事件
            }
        }
    }
}
