package org.lilu.sns.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lilu.sns.pojo.Result;
import org.lilu.sns.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void selectUserById() {

    }

    @Test
    public void login() {
        Result result = userService.login("t","12345");
        System.out.println(result);
    }

    @Test
    public void register() {
        User user = new User();
        user.setLoginName("1");
        user.setPassword("123");
        user.setEmail("avbc");
        Result result = userService.register(user);
    }
}