package com.learn.rabbitmq_learn.quickstarter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Classname producter
 * @Description
 * @Date 2020/4/19 10:08
 * @Created by lyf
 */
public class producter {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建连接工厂
        ConnectionFactory connectionFactory =  new ConnectionFactory();
        //1.1设置connectionFactory参数
        connectionFactory.setHost("192.168.137.111");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        //2.通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3.通过connection创建一个channel
        Channel channel = connection.createChannel();

        //4.通过channel发送数据
        for (int i = 0; i <5 ; i++) {
            String msg = "hello RabbitaMQ";
            channel.basicPublish("","test001",null,msg.getBytes());
        }
        //5.记得关闭相关连接
        channel.close();
        connection.close();
    }
}
