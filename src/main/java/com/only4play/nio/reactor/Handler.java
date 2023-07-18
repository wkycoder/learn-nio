package com.only4play.nio.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 处理器，处理读写事件
 * 一个SocketChannel对应一个Handler
 * 或者称之为Processor
 *
 * // 改为监听Write就绪事件，interestOps会清空原先监听的事件
 * // selectionKey.interestOps(SelectionKey.OP_WRITE);
 *
 * @author wuming
 * @date 2023/4/17/04/17 21:34
 */
public class Handler implements Runnable {

    private final SocketChannel socketChannel;

    private final SelectionKey selectionKey;

    public Handler(SocketChannel socketChannel, SelectionKey selectionKey) {
        this.socketChannel = socketChannel;
        this.selectionKey = selectionKey;
    }

    @Override
    public void run() {
        if (selectionKey.isReadable()) {
            // 读取数据
            try {
                read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void read() throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        System.out.println("开始读取客户端消息");
        int length = socketChannel.read(allocate);
        if (length > 0) {
            System.out.println("读取到客户端发送的数据：" + new String(allocate.array(), 0, length, StandardCharsets.UTF_8));
        }
        if (length == -1) {
            // 为-1表示未读取到数据，可能是客户端主动关闭了连接
            socketChannel.close();
        }
        // 向客户端发送响应
        socketChannel.write(ByteBuffer.wrap("我收到了你发的消息".getBytes(StandardCharsets.UTF_8)));
    }

}
