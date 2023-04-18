package com.only4play.nio.reactor;

import java.io.IOException;
import java.nio.channels.*;

/**
 * 接收器，用于创建客户端连接通道
 *
 * @author wuming
 * @date 2023/4/17/04/17 21:34
 */
public class Acceptor implements Runnable {

    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    private final SelectionKey selectionKey;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel, SelectionKey selectionKey) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
        this.selectionKey = selectionKey;
    }

    @Override
    public void run() {
        try {
            // 接收客户端的连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                socketChannel.configureBlocking(false);
                // 注册读就绪时间，即去读取通道中准备就绪的数据 （默认的）
                SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                // 设置回调
                selectionKey.attach(new Handler(socketChannel, selectionKey));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
