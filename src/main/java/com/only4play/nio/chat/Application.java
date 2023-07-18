package com.only4play.nio.chat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author wuming
 * @date 2023/7/17/07/17 18:38
 */
public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 创建客户端和服务端
        ChatServer chatServer = new ChatServer(8090);
        // 启动服务器
        new Thread(()-> {
            try {
                chatServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        TimeUnit.SECONDS.sleep(1);

        ChatClient client1 = new ChatClient("localhost", 8090, "wuming");
        ChatClient client2 = new ChatClient("localhost", 8090, "zhangsan");
        ChatClient client3 = new ChatClient("localhost", 8090, "lisi");

        // 接收服务器发送的消息
        new Thread(() -> {
            while (true) {
                try {
                    client1.receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 接收服务器发送的消息
        new Thread(() -> {
            while (true) {
                try {
                    client2.receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 接收服务器发送的消息
        new Thread(() -> {
            while (true) {
                try {
                    client3.receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);
        // 发送消息到服务器
        new Thread(() -> {
            try {
                client1.send("first msg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }


}
