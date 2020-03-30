package com.demo.bio.server1;

import java.io.*;
import java.net.Socket;

/**
 * @Classname ChatHandler
 * @Description
 * @Date 2020/3/24 21:54
 * @Created by lyf
 */
public class ChatHandler implements Runnable {
    private ChatServer chatServer;
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //存储新上线的消息
            chatServer.addClient(socket);
            //读取用户发送的消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = null;
            while((msg=reader.readLine())!=null){
                String fwdMsg = "客户端："+socket.getPort()+msg+"\n";
                System.out.print(fwdMsg);
                //将服务器收到的信息转发给在线用户
                chatServer.forwardMessage(socket,fwdMsg);
                //检查用户是否准备退出
                if (chatServer.readtOQuit(msg)){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
