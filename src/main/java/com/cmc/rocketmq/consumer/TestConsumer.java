package com.cmc.rocketmq.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "test-topic", consumerGroup = "my-consumer-group")
@ConditionalOnProperty(
        name = "project.mq.enable",
        havingValue = "true"
)
public class TestConsumer implements RocketMQListener<String> {


    @Override
    public void onMessage(String s) {
        System.out.println("收到消息" + s);
    }
}
