package com.yoyo.admin.kafka_common.producer;

import java.text.ParseException;

public interface SendCallBack {

    /**
     * 生产成功回调
     * @param topic topic
     * @param msg 信息字符串
     */
    void sendSuccessCallBack(String topic,String msg) throws ParseException;

    /**
     * 生产失败回调
     * @param topic topic
     * @param msg 信息字符串
     * @param ex 异常
     */
    void sendFailCallBack(String topic,String msg,Throwable ex) throws ParseException;

}
