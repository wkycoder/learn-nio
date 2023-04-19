package com.only4play.nio;

import java.io.IOException;

/**
 * @author wuming
 * @date 2023/4/16/04/16 22:19
 */
public class Application {


    public static void main(String[] args) throws IOException, InterruptedException {
        NioClient client = new NioClient("127.0.0.1", 8091);
        client.send("这是第一条消息");
        // 主动关闭连接
        // 客户端主动关闭的时候会向通道写入数据，selector会监听到一个OP_READ事件
        // 然后执行读取操作，但是读取的时候通道已经关闭了，因此读取到字节数是-1，然后就会执行close操作，关闭socket
        client.close();
    }



}
