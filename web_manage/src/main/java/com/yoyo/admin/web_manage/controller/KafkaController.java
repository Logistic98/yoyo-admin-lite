package com.yoyo.admin.web_manage.controller;

import com.alibaba.fastjson.JSON;
import com.yoyo.admin.common.utils.ResultDataUtils;
import com.yoyo.admin.kafka_common.producer.KafkaAnalyzeProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


@Api(tags = "Kafka数据管理")
@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    @Autowired
    private KafkaAnalyzeProducer kafkaAnalyzeProducer;

    @ApiOperation("向Kafka的指定Topic同步发送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", value = "Topic路径", dataType = "String", required = true, paramType = "body"),
            @ApiImplicitParam(name = "message", value = "消息数据", dataType = "Map", required = true, paramType = "body"),
    })
    @PostMapping("/sendKafkaSyncMessage")
    public ResponseEntity<?> sendKafkaSyncMessage(@RequestBody @ApiIgnore Map<String, Object> data) {
        String topic = data.get("topic").toString();
        String message = JSON.toJSONString(data.get("message"));
        try {
            // 向Kafka的指定Topic同步发送消息
            kafkaAnalyzeProducer.sendSynchronize(topic, message);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

    @ApiOperation("向Kafka的指定Topic异步发送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", value = "Topic路径", dataType = "String", required = true, paramType = "body"),
            @ApiImplicitParam(name = "message", value = "消息数据", dataType = "Map", required = true, paramType = "body"),
    })
    @PostMapping("/sendKafkaAsyncMessage")
    public ResponseEntity<?> sendKafkaAsyncMessage(@RequestBody @ApiIgnore Map<String, Object> data) {
        String topic = data.get("topic").toString();
        String message = JSON.toJSONString(data.get("message"));
        try {
            // 向Kafka的指定Topic异步发送消息
            kafkaAnalyzeProducer.sendAsynchronize(topic, message);
            return ResultDataUtils.success();
        } catch (Exception ex) {
            return ResultDataUtils.error(ex.getMessage());
        }
    }

}