package com.example.mynetty.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 创建循环组
        EventLoopGroup group = new NioEventLoopGroup(2);//功能比较全面，可以处理io,普通任务 定时任务 2为指定创建几个线程
        // DefaultEventLoopGroup eventExecutors = new DefaultEventLoopGroup(); 只能处理普通任务和定时任务
        // 获取下一个事件循环对象
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        // 执行普通任务
        group.next().submit(() -> {
            log.debug("ok");
        });
        // 执行定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.debug("okk");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
