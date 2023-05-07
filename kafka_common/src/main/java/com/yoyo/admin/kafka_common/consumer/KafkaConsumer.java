package com.yoyo.admin.kafka_common.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * kafka消费者
 */
@Component
@Slf4j
public class KafkaConsumer {

    /**
     * kafka的监听器 消费消息
     * @param record
     * @param item
     */
    @KafkaListener(topics = "yoyo_admin_topic", groupId = "spring_customer")
    public void topicListener(ConsumerRecord<String, String> record, Acknowledgment item) {
        log.info("开始消费：{}==,{}==,{};",record.topic(),record.partition(),record.value());
        item.acknowledge(); // 手动提交
    }

}