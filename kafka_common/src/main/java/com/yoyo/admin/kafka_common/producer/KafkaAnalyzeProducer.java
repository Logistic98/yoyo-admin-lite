package com.yoyo.admin.kafka_common.producer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * kafka生产者
 */
@Slf4j
@Component
public class KafkaAnalyzeProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送数据到kafka
     *
     * @param topic        topic名称
     * @param message      发送信息字符串
     * @param sendCallBack 发送回调
     */
    public void send(String topic, String message, SendCallBack sendCallBack) {

        ListenableFuture listenableFuture = kafkaTemplate.send(topic, message);
        // 发送成功后回调
        SuccessCallback<String> successCallback = new SuccessCallback() {
            @SneakyThrows
            @Override
            public void onSuccess(Object result) {
                sendCallBack.sendSuccessCallBack(topic, message);
            }
        };
        // 发送失败回调
        FailureCallback failureCallback = new FailureCallback() {
            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                sendCallBack.sendFailCallBack(topic, message, ex);
            }
        };

        listenableFuture.addCallback(successCallback, failureCallback);
    }

    /**
     * producer 同步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendSynchronize(String topic, String message) {
        kafkaTemplate.send(topic, message).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("----事件kafka记录解析完成放入topic：{}，发送失败{}", topic, message, throwable);
            }

            @Override
            public void onSuccess(Object o) {
                log.info("----事件kafka记录解析完成放入topic：{}，发送成功:{}", topic, message);
            }
        });
    }

    /**
     * producer 异步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendAsynchronize(String topic, String message) throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic, message).get(10, TimeUnit.SECONDS);
    }

}
