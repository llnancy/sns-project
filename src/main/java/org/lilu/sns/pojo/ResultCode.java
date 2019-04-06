package org.lilu.sns.pojo;

/**
 * @Auther: lilu
 * @Date: 2019/1/27
 * @Description: 统一管理响应结果状态码
 */
public enum ResultCode {
    /**
     * 默认code=0请求成功；code=-1请求失败
     */
    SUCCESS(0,"请求成功"),
    FAIL(-1,"请求失败"),

    USER_NOT_EXIST(400,"用户不存在"),

    LOGOUT_SUCCESS(200,"退出成功"),
    USERNAME_EMAIL_NOT_EXIST(400,"用户名或邮箱不存在"),
    USERNAME_EMAIL_REGISTERED(400,"用户名或邮箱已经被注册"),
    PASSWORD_WRONG(400,"密码不正确"),
    LOGIN_SUCCESS(200,"登录成功"),
    REGISTER_SUCCESS(200,"注册成功"),
    AUTH_SUCCESS(200,"激活成功"),
    NOT_AUTH_SUCCESS(401,"账号未激活"),

    LOGIN_EXPIRED(-3,"登录身份已过期，请重新登录"),
    LOGGED_IN(-4,"已经登录，请勿重复登录"),
    NOT_LOGGED_IN(-3,"未登录，请先登录"),

    QUESTION_ADD_SUCCESS(200,"问题发布成功"),
    COMMENT_ADD_SUCCESS(200,"评论发表成功"),
    MESSAGE_ADD_SUCCESS(200,"消息发送成功"),

    SERVER_DATA_EXCEPTION(500,"服务器数据异常"),
    PARAMETER_NOT_VALID(500,"输入参数不合法"),
    SYSTEM_ERROR(404,"系统错误"),
    RESOURCE_NOT_EXIST(404,"资源不存在")
    ;

    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}