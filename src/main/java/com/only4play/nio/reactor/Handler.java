package com.only4play.nio.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 处理器，处理读写事件
 * 一个SocketChannel对应一个Handler
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
        if (selectionKey.isWritable()) {
            // 写数据
            write();
        }
    }

    private void read() throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        int read = socketChannel.read(allocate);
        System.out.println("读取到客户端发送的数据：" + new String(allocate.array(), 0, read, StandardCharsets.UTF_8));
        // 改为监听Write就绪事件，interestOps会清空原先监听的事件
//        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    private void write() {
        System.out.println("....");
    }

}
