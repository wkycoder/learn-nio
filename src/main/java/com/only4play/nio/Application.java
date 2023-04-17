package com.only4play.nio;

import java.io.IOException;

/**
 * @author wuming
 * @date 2023/4/16/04/16 22:19
 */
public class Application {


    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient("127.0.0.1", 8089);
        client.send("这是第一条消息");
    }



}
