package com.example.task_3.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;

public class Send {
  private static final String QUEUE_NAME="hello";
  public static void main(String[] argv) throws Exception{
    ConnectionFactory factory=new ConnectionFactory();
    factory.setHost("localhost");
    try(Connection connection= factory.newConnection();
        Channel channel= connection.createChannel()){
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      String msg="Hello World!";
      channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '"+msg+"'");
    }
  }
}
