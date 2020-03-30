package com.demo.bio.server1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname ChatServer
 * @Description
 * @Date 2020/3/24 21:54
 * @Created by lyf
 */
public class ChatServer {
    private final int DEFAULT_PORT = 8888;
    private final String QUIT = "quit";


    private ExecutorService executorService;
    private ServerSocket serverSocket;
    private Map<Integer, Writer> connectedClients;

    public ChatServer() {
        executorService = Executors.newFixedThreadPool(10);
        this.connectedClients = new HashMap<Integer, Writer>();
    }

    public synchronized void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(port, writer);
            System.out.println("客户端：" + port + "已经连接客户端");
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            if (connectedClients.containsKey(port)) {
                connectedClients.get(port).close();
            }
            connectedClients.remove(port);
            System.out.println("客户端：" + port + "已经断开连接");
        }
    }

    public void forwardMessage( Socket socket, String fwdMsg) throws IOException {
        for (Integer n : connectedClients.keySet()) {
            if (!n.equals(socket.getPort())) {
                Writer writer = connectedClients.get(n);
                 writer.write(fwdMsg);
                writer.flush();
            }
        }
    }
    public void start(){
        try {
            //绑定监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口："+DEFAULT_PORT);
            while(true){
                //等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println(socket.getPort());
                //创建ChatHandler线程
                executorService.execute(new ChatHandler(this,socket));;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }

    }
    public void close(){
        if (serverSocket !=null){
            try {
                serverSocket.close();
                System.out.println("关闭serversocket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean readtOQuit(String msg){
         return QUIT.equals(msg);
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
