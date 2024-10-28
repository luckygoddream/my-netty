package com.example.mynetty.chapter1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        // 1创建selector,管理多个channel
        Selector boss = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        // 2建立selector和channel的联系（注册）
        // SelectionKey就是将来事件发生后，通过它可以知道事件和那个channel的事件
        SelectionKey sscKey = ssc.register(boss, 0, null);
        // 只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        //3.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        // 创建worker
        Worker worker = new Worker("worker-0");

        Worker [] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();
        // worker.register();
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}", sc.getRemoteAddress());
                    log.debug("before register...{}", sc.getRemoteAddress());
                    // 取余轮询
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    // worker.register(sc);
                    // 关联
                    // sc.register(worker.selector, SelectionKey.OP_READ, null);
                    log.debug("after register...{}", sc.getRemoteAddress());
                }
            }
        }

    }

    static class Worker implements Runnable {
        private Thread thread;

        private Selector selector;

        private String name;

        private boolean start = false;

        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        // 初始化
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                this.selector = Selector.open();
                this.thread = new Thread(this, name);
                this.thread.start();
                this.start = true;
            }
            // 向队列添加任务，但这个任务并没执行
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();//唤醒select方法
        }


        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
