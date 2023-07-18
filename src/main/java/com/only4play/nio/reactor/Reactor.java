package com.only4play.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Reactor（事件分离器）
 * 用于分离连接事件和读写事件
 *
 * @author wuming
 * @date 2023/4/17/04/17 21:33
 */
public class Reactor implements Runnable {

    private final Selector selector;

    public Reactor(int port) {
        try {
            // 创建通道和selector
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 绑定端口号
            InetSocketAddress address = new InetSocketAddress(port);
            serverSocketChannel.socket().bind(address);
            // 配置为非阻塞
            serverSocketChannel.configureBlocking(false);
            // 接收accept事件，附加对象（Acceptor），用于回调
            SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(new Acceptor(selector, serverSocketChannel));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Reactor创建失败，原因：" + e.getMessage());
        }
    }


    @Override
    public void run() {
        try {
            // 轮询
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 将接收到的事件进行分发
                    dispatch(key);
                    // 移除处理过的事件
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        // 取出key的附加对象
        Runnable task = (Runnable) key.attachment();
        if (task != null) {
            task.run();
        }
    }


}
