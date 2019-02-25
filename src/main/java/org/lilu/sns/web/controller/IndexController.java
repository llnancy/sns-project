package org.lilu.sns.web.controller;

import org.lilu.sns.pojo.Result;
import org.lilu.sns.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: lilu
 * @Date: 2019/1/22
 * @Description:
 */
@RestController
public class IndexController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public Result test() {
        System.out.println("控制器层代码执行了。。。。");
        return Result.success().put("test",testService.test());
    }
}