package org.lilu.sns.exception;

/**
 * @Auther: lilu
 * @Date: 2019/1/31
 * @Description: 实体更新（增删改）记录异常：sql语句返回的影响的记录行数与实际应该被影响的行数不同时抛出该异常
 */
public class EntityUpdateException extends RuntimeException {
    public EntityUpdateException(String message) {
        super(message);
    }
}