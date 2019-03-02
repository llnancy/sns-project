package org.lilu.sns.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/1/25
 * @Description: 统一json返回格式
 */
public class Result implements Serializable {
    private int code;
    private String msg;
    private Map<String,Object> data = new HashMap<>();

    /**
     * 使用ResultCode枚举统一管理返回的状态信息
     * @param resultCode
     */
    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    /**
     * 默认请求成功的方法
     * @return
     */
    public static Result success() {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 默认请求失败的方法
     * @return
     */
    public static Result fail() {
        return new Result(ResultCode.FAIL);
    }

    /**
     * 提供自定义响应信息的方法
     * @param resultCode
     * @return
     */
    public static Result info(ResultCode resultCode) {
        return new Result(resultCode);
    }

    /**
     * 支持链式操作的添加data数据的响应信息的put方法
     * @param key
     * @param value
     * @return
     */
    public Result put(String key, Object value) {
        this.getData().put(key,value);
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Map<String, Object> getData() {
        return data;
    }
}