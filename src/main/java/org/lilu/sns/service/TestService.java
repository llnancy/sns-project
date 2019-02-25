package org.lilu.sns.service;

import org.lilu.sns.exception.EntityUpdateException;
import org.springframework.stereotype.Service;

/**
 * @Auther: lilu
 * @Date: 2019/1/31
 * @Description:
 */
@Service
public class TestService {
    public String test() {
        if (2 != 1) {
            throw new EntityUpdateException("测试service抛出异常");
        }
        return "test";
    }
}