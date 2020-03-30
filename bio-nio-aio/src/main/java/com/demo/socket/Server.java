package com.demo.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Classname Server
 * @Description
 * @Date 2020/3/24 0:19
 * @Created by lyf
 */
public class Server {
    public static void main(String[] args) {
        final String QUIT = "quit";
        final int DEFAULT_PROT = 8899;
        ServerSocket serverSocket = null;
        try {
            //绑定监听端口
            serverSocket = new ServerSocket(DEFAULT_PROT);
            System.out.println("启动服务器，监听" + DEFAULT_PROT);
            while (true) {
                //等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端：" + socket.getPort() + "已连接");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                //读取客户端发送的消息
                String msg = null;
                while ((msg = reader.readLine()) != null) {
                    System.out.println("客户端" + socket.getPort() + ",msg:" + msg);
                    //回复客户端的信息
                    writer.write("服务器：" + msg + "\n");
                    writer.flush();
                    if (QUIT.equals(msg)){
                        System.out.println("客户端："+socket.getPort()+"关闭");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
