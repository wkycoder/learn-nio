package com.only4play.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author wuming
 * @date 2023/4/16/04/16 22:06
 */
public class NioClient {

    private final SocketChannel socketChannel;

    public NioClient(String ip, int port) {
        // 创建连接通道
        try {
            socketChannel = SocketChannel.open();
            // 设置要连接的ip和端口号
            InetSocketAddress address = new InetSocketAddress(ip, port);
            socketChannel.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("连接服务器失败, 原因：" + e.getMessage());
        }
    }

    /**
     * 向服务端发送消息
     *
     * @param msg
     */
    public void send(String msg) {
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
//            // 读取服务端返回的数据
//            ByteBuffer allocate = ByteBuffer.allocate(1024);
//            int read = socketChannel.read(allocate);
//            System.out.println("服务端返回的消息：" +
//                    new String(allocate.array(), 0, read, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("消息发送失败, 原因：" + e.getMessage());
        }
    }

    public void close() throws IOException {
        socketChannel.close();
    }


}
