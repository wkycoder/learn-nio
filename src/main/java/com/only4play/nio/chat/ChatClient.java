package com.only4play.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 聊天室客户端
 *
 * @author wuming
 * @date 2023/7/17/07/17 15:35
 */
public class ChatClient {

    private final SocketChannel socketChannel;

    /**
     * 当前用户名称
     */
    private final String username;

    public ChatClient(String host, Integer port, String username) throws IOException {
        socketChannel = SocketChannel.open();
        // 非阻塞模式
        socketChannel.configureBlocking(false);
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            if (!socketChannel.connect(address)) {
                // 循环等待连接完成 （自旋）
                while (!socketChannel.finishConnect()) {
                    // sleep 1s
                    System.out.println("connecting...");
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            System.out.println(username + " connect success, " + socketChannel.getLocalAddress().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("无法连接到服务器，host=%s，port=%s", host, port));
        }
        this.username = username;
    }


    /**
     * 发送消息到服务器，广播消息，群聊
     * @param msg
     */
    public void send(String msg) throws IOException {
        msg = username + "-" + msg;
        socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 接收服务器发送的消息
     */
    public void receive() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length = socketChannel.read(buffer);
        if (length > 0) {
            System.out.println(username + " 收到服务端发送的消息：" + new String(buffer.array(), 0, length, StandardCharsets.UTF_8));
        }
    }

    /**
     * 关闭socket连接
     * @throws IOException
     */
    public void close() throws IOException {
        socketChannel.close();
    }




}
