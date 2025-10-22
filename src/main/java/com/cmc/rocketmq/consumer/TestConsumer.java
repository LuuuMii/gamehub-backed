package com.cmc.rocketmq.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

//@Service
//@RocketMQMessageListener(topic = "test-topic", consumerGroup = "my-consumer-group")
public class TestConsumer implements RocketMQListener<String> {


    @Override
    public void onMessage(String s) {
        System.out.println("收到消息" + s);
    }
}
