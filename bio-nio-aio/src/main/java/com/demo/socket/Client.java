package com.demo.socket;

import java.io.*;
import java.net.Socket;

/**
 * @Classname Client
 * @Description
 * @Date 2020/3/24 0:23
 * @Created by lyf
 */
public class Client {
    public static void main(String[] args) {
        final String QUIT = "quit";
        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        final int DEFAULT_SERVER_PORT = 8899;
        Socket socket = null;
        BufferedWriter writer = null;
        try {
            //创建socket
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);
            //创建io流
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer =  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //等待用户输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();
                //发送给服务器
                writer.write(input+"\n");
                writer.flush();

                //读取服务器返回的消息
                String msg = reader.readLine();
                System.out.println(msg);
                if (QUIT.equals(input)){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (writer!=null){
                try {
                    writer.close();
                    System.out.println("关闭socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
