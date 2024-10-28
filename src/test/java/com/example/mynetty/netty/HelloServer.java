package com.example.mynetty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        // 启动器 负责组装netty组件启动服务器
        new ServerBootstrap()
                // group 组
                .group(new NioEventLoopGroup())
                // 选择服务器端的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                // 决定了 能执行哪些操作
                .childHandler(
                        // 代表和客户端进行数据读写的通道，Initializer初始化，负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 添加具体handler
                                ch.pipeline().addLast(new StringDecoder());//将bytebuf转换为字符串
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {//自定义handler
                                    // 读事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        //     绑定监听端口
                        }).bind(8080);

    }
}
