package com.only4play.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuming
 * @date 2023/7/17/07/17 12:22
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        // 创建服务端channel
        ServerSocketChannel channel = ServerSocketChannel.open();
        // 绑定端口号
        channel.bind(new InetSocketAddress(8080));
        // 配置为非阻塞模式
        channel.configureBlocking(false);

        // 创建selector，并为服务端channel注册accept事件
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);

        // 轮询事件并处理
        while (true) {
            // 检查是否存在事件
            if (selector.select() == 0) {
                continue;
            }
            // 获取所有的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 处理客户端连接请求
                if (selectionKey.isAcceptable()) {
                    System.out.println("开始处理客户端连接...");
                    // 拿到对应的channel对象
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    // 将客户端通道注册到selector上，并监听读操作，后面我们可以从这个通道中读取客户端写入的数据
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                // 读取客户端写入的数据
                if (selectionKey.isReadable()) {
                    // 获取对应的socketChannel
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 将数据读取到缓冲区中
                    int length = socketChannel.read(buffer);
                    System.out.println("收到客户端发送的数据：" + new String(buffer.array(), 0, length));
                }
                // 移除已处理的事件
                iterator.remove();
            }
        }

    }


}
