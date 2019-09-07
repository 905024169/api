package com.test.util;

import java.io.Serializable;


/**
 * 返回响应类
 * @author duanwei
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String msg;
    private Object result;



    public Response() {
        this.code = 0;
        this.msg = "请求成功";
    }

    public Response(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    public Response(Object result) {
        this.code = 0;
        this.msg = "请求成功";
        this.result = result;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

