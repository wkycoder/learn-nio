package com.only4play.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author wuming
 * @date 2023/7/17/07/17 11:34
 */
public class NioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 创建客户端channel
        SocketChannel socketChannel = SocketChannel.open();
        // 设置为非阻塞模式
        socketChannel.configureBlocking(false);
        // 设置服务器的ip和端口
        InetSocketAddress address = new InetSocketAddress("localhost", 8080);
        // 连接服务器
        boolean connect = socketChannel.connect(address);
        if (!connect) {
            // 判断是否完成连接，连接创建完成之后才能去发送消息
            while (!socketChannel.finishConnect()) {
                TimeUnit.SECONDS.sleep(2);
            }
        }

        // 准备缓存区数据，并往通道中写入数据
        ByteBuffer buffer = ByteBuffer.wrap("one msg".getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer);
        System.out.println("消息发送成功");

        // 阻塞客户端线程，用于测试
        new Scanner(System.in).nextLine();
        // 客户端主动断开连接的时候，会向通道中写入数据，因此selector会监听到一个读事件

    }




}
