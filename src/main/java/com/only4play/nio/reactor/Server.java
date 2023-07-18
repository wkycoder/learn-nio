package com.only4play.nio.reactor;


import com.only4play.nio.NioClient;

import java.io.IOException;

/**
 * @author wangkunyang
 * @date 2023/04/18 10:00
 */
public class Server {

    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(8091);
        Thread thread = new Thread(reactor);
        thread.start();

        new Thread(() -> {
            NioClient client = new NioClient("localhost", 8091);
            client.send("one msg");
        }).start();
    }

}
