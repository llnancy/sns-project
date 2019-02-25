package org.lilu.sns.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lilu.sns.pojo.User;
import org.lilu.sns.util.CodingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    public void initDatabase() {
        Random random = new Random();
//        for (int i = 0; i < 10; i++) {
//            User user = new User();
//            user.setLoginName(String.format("user%d",i));
//            user.setNickName(String.format("我是一把格子伞%d",i));
//            user.setPassword("123456");
//            user.setAvatar(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
//            user.setGender("蒙面侠");
//            user.setSalt("");
//            userDao.insert(user);
//        }
        User user = new User();
        user.setLoginName("user");
        user.setNickName("我是一把格子伞");
        user.setPassword("123456");
        user.setAvatar(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
        user.setGender("蒙面侠");
        user.setSalt("654321");
        System.out.println(user);
        int userId = userDao.insert(user);
        Assert.assertNotNull(userId);
//        Assert.assertEquals(14,userId);
        System.out.println(userId);
        // @insert插入时设置了useGeneratedKeys属性，
        // 数据插入成功后，方法仍然后数据库中影响的记录行数，
        // 主键id值被反填到了user对象中，调用user.getId()即可获得
        System.out.println(user);
    }

    @Test
    public void selectByIdTest() {
        User user = userDao.selectById(1);
        System.out.println(user);
    }

    @Test
    public void selectByLoginNameTest() {
        User user = userDao.selectByLoginName("李路");
        System.out.println(user);
    }

    @Test
    public void selectByEmailTest() {
        User user = userDao.selectByEmail("tclilu@lilu.org.cn");
        System.out.println(user);
    }
}