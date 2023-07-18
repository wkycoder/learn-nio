package com.only4play.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wuming
 * @date 2023/7/17/07/17 16:16
 */
public class ChatServer {


    private ServerSocketChannel serverSocketChannel;

    /**
     * selector选择器
     */
    private Selector selector;

    public ChatServer(Integer port) throws IOException {
        // 开启通道
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        // 绑定端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        // 创建selector
        selector = Selector.open();
        // 注册ServerSocketChannel到selector，监听accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 启动服务器
     */
    public void start() throws IOException {
        while (true) {
            // 监听通道上的事件，返回的是发生事件的通道数
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }
            // 发生事件的key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    // 广播上线消息
                    broadcastOtherClient(socketChannel.getRemoteAddress().toString() + "上线了", socketChannel);
                }
                // 读取客户端发送的数据，客户端write，服务端read
                if (key.isReadable()) {
                    read(key);
                }
                iterator.remove();
            }
        }

    }


    /**
     * 读取并转发数据
     * @param selectionKey
     */
    public void read(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 创建缓冲区并将channel中的数据读取到缓冲区中
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length = socketChannel.read(buffer);
        if (length > 0) {
            String msg = new String(buffer.array(), 0, length, StandardCharsets.UTF_8);
            System.out.println("收到客户端发送的消息：" + msg);
            // 将消息广播至其他客户端
            broadcastOtherClient(msg, socketChannel);
        }
        if (length == -1) {
            // 关闭客户端连接
            socketChannel.close();
        }
    }

    /**
     * 广播消息到其他客户端
     *
     * write
     * @param msg
     * @param needExcludeChannel
     * @throws IOException
     */
    private void broadcastOtherClient(String msg, SocketChannel needExcludeChannel) throws IOException {
        Set<SelectionKey> allKeys = selector.keys();
        for (SelectionKey key : allKeys) {
            SelectableChannel channel = key.channel();
            // 只关心SocketChannel且排除指定的key
            if (channel instanceof SocketChannel
                    && !channel.equals(needExcludeChannel)) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                // 写入消息
                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }


}
