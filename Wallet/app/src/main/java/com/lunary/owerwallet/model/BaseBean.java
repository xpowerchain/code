package com.lunary.owerwallet.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/3.
 */
public class BaseBean<T>{
    private String code;
    private String msg;
    private T data;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
