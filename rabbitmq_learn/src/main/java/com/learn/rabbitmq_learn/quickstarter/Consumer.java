package com.learn.rabbitmq_learn.quickstarter;

import com.rabbitmq.client.*;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Classname Consumer
 * @Description
 * @Date 2020/4/19 10:08
 * @Created by lyf
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
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

        //4.创建一个队列 queueDeclare (队列名，是否持久化，是否独占，是否自动删除,扩展参数)
        String queueName = "test001";
        channel.queueDeclare(queueName,true,false,false,null);

        //5.创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //6.设置channel
        channel.basicConsume(queueName,true,queueingConsumer);

        //7.获取消息

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("消费端"+msg);
           // Envelope envelope = delivery.getEnvelope();
        }
    }
}
