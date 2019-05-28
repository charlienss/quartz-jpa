package com.example.quartsdemo.entity;


import lombok.Data;

import java.util.List;

/**
 * 用于layui的返回格式
 */
@Data
public class ResultBody {

    private int code;
    private String msg;
    private long count;
    private List<JobEntity> data;

    public ResultBody() {
    }

    public ResultBody(int code, String msg, long count, List<JobEntity> data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }
}
