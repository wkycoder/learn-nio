package com.only4play.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wangkunyang
 * @date 2022/08/26 14:25
 */
public class NioPlainEchoServer {

    /**
     * 启动服务器
     *
     * @param port
     * @throws IOException
     */
    public void start(int port) throws IOException {
        System.out.println("Listening for connections on port " + port);
        // 创建服务端通道
        ServerSocketChannel channel = ServerSocketChannel.open();
        ServerSocket ss = channel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address);
        // 配置为非阻塞模式
        channel.configureBlocking(false);
        // 创建Selector
        Selector selector = Selector.open();
        // ops:Operation Set，操作集  注册通道到选择器
        channel.register(selector, SelectionKey.OP_ACCEPT);
        // 循环处理通道中的事件
        while (true) {
            // 检查注册的通道中是否有事件产生
            int select = selector.select();
            if (select == 0) {
                // 不存事件
                continue;
            }
            // 获取对应事件集合
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                try {
                    // 接收客户端的连接请求
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        // 将客户端通道注册到selector上  读和写
                        client.register(selector, SelectionKey.OP_READ);
//                        client.register(selector, SelectionKey.OP_WRITE);
                    }
                    // 读取客户端通道的数据（客户端写入的数据）
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
//                        ByteBuffer output = (ByteBuffer) key.attachment();
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        int read = client.read(allocate);
                        System.out.println("接收到客户端发送的消息：" + new String(allocate.array(), 0, read, StandardCharsets.UTF_8));
                        // 向客户端回写数据
                        client.write(allocate);
                    }
                    // 处理写请求
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        client.write(output);
                        output.compact();
                    }
                } catch (Exception e) {
                    key.cancel();
                    key.channel().close();
                }
                // 移除已处理的事件
                iterator.remove();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        NioPlainEchoServer server = new NioPlainEchoServer();
        server.start(8089);
    }



}
