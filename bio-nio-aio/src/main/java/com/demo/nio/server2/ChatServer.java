package com.demo.nio.server2;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * @Classname ChatServer
 * @Description
 * @Date 2020/3/26 0:05
 * @Created by lyf
 */
public class ChatServer {
    private final int DEFAULT_PORT = 8888;
    private final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer rBuffer = ByteBuffer.allocate(BUFFER);
    private ByteBuffer wBuffer = ByteBuffer.allocate(BUFFER);
    private Charset charset = Charset.forName("UTF-8");
    private int port;

    public ChatServer(int port) {
        //自定义端口
        this.port = port;
    }

    private void start(){
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));

            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口："+port +"...");
            while (true) {
                //阻塞式调用
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.stream().forEach(key -> {
                    try {
                        handles(key);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (ClosedSelectorException e){
                        //用户正常退出
                    }finally {
                        close(selector);
                    }
                });
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(selector);
        }

    }
    private String getClientName(SocketChannel client){
        return "客户端【"+client.socket().getPort();
    }
    private void handles(SelectionKey key) throws IOException {
        //accept事件 - 和客户端建立连接
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);
            System.out.println(getClientName(client)+"】已连接");
        }
        //read 事件 - 客户端发送了消息
        else  if (key.isReadable()){
            SocketChannel client = (SocketChannel) key.channel();
            String fwdMsg = receive(client);
            if (fwdMsg.isEmpty()){
                //客户端异常
                key.cancel();
                selector.wakeup();//
            }else {
                //转发数据
                forwardMessage(client,fwdMsg);
                //检查用户是否退出
                if (readyToQuit(fwdMsg)) {
                    key.cancel();
                    selector.wakeup();
                    System.out.println();
                    System.out.println(getClientName(client)+"】已断开");
                }
            }
        }
    }

    private void forwardMessage(SocketChannel client, String fwdMsg) {
        selector.keys()
                .stream()
                .filter(key->!(key.channel() instanceof  ServerSocketChannel)
                                 ||key.isValid()
                                ||!client.equals(key.channel()))
                .map(key->key.channel())
                .forEach(key->{
                     wBuffer.clear();
                     wBuffer.put(charset.encode(getClientName(client)+":"+fwdMsg));
                     wBuffer.flip();
                     while (wBuffer.hasRemaining()){
                         try {
                             ((SocketChannel)key).write(wBuffer);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                });
    }

    private String receive(SocketChannel client) throws IOException {
        rBuffer.clear();
        while(client.read(rBuffer)>0);
        rBuffer.flip(); //读转写
        return String.valueOf(charset.decode(rBuffer));
    }

    private boolean readyToQuit(String msg){
        return QUIT.equals(msg);
    }
    public void close(Closeable closeable){
        if (closeable !=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(7777);
        chatServer.start();
    }
}
