package org.lilu.sns.exception;

/**
 * @Auther: lilu
 * @Date: 2019/2/10
 * @Description: 自定义系统错误异常
 */
public class SystemErrorException extends RuntimeException {
    public SystemErrorException(String message) {
        super(message);
    }
}
