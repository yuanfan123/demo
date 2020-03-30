package com.demo.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

/**
 * @Classname UserInputHandle
 * @Description
 * @Date 2020/3/24 21:55
 * @Created by lyf
 */
public class UserInputHandle implements Runnable {
    private ChatClient chatClient;

    public UserInputHandle(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        try {
            //等待用户输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {

                String input = consoleReader.readLine();
                //向服务器发送消息
                chatClient.send(input);
                //检查用户是否准备退出
                if(chatClient.readyToQuit(input)){
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
