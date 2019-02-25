package org.lilu.sns.exception;

import org.lilu.sns.pojo.Result;
import org.lilu.sns.pojo.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description: 异常处理器
 */
@RestControllerAdvice
public class MyExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    /**
     * 处理对控制器方法参数校验产生的异常ConstraintViolationException
     * @param e
     * @return 返回带有错误信息的json
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleValidationException(ConstraintViolationException e) {
        Map<Object,Object> exceptions = new HashMap<>();
        Set<ConstraintViolation<?>> cvs = e.getConstraintViolations();
        for (ConstraintViolation<?> cv : cvs) {
            exceptions.put(cv.getInvalidValue(),cv.getMessage());
        }
        // 日志记录
        logger.info("参数校验exceptions：",exceptions);
        return Result.info(ResultCode.PARAMETER_NOT_VALID);
    }

    @ExceptionHandler(EntityUpdateException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleEntityUpdateException(EntityUpdateException e) {
        logger.error("实体增删改异常：" + e.getMessage());
        return Result.info(ResultCode.SERVER_DATA_EXCEPTION);
    }

    @ExceptionHandler(SystemErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleSystemErrorException(SystemErrorException e) {
        logger.error("自定义的系统异常：" + e.getMessage());
        return Result.info(ResultCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleException(Exception e) {
        logger.error("异常：" + e.getMessage());
        return Result.info(ResultCode.SYSTEM_ERROR);
    }
}