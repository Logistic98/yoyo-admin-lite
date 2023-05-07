package com.yoyo.admin.common.logger.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogContentObject implements Serializable {

    private String name;

    private Object data;

}
