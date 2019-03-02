package org.lilu.sns.service;

import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventProducer;
import org.lilu.sns.async.EventType;
import org.lilu.sns.dao.UserDao;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.pojo.Result;
import org.lilu.sns.pojo.ResultCode;
import org.lilu.sns.pojo.User;
import org.lilu.sns.util.CodingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description: 登录/注册业务层
 */
@Service
@Validated
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 根据id查询user
     * @param id
     * @return
     */
    public User selectUserById(int id) {
        return userDao.selectById(id);
    }

    /**
     * 如果输入的登录名是邮箱类型，则使用邮箱登录，否则使用用户名登录
     *
     * @Pattern(regexp = "(^[a-zA-Z0-9_-]{2,16}$)|(^[\\u2E80-\\u9FFF]{2,8}$)",
     *          message = "用户名必须是2~8位中文或者2~16位英文和数字组合")
     *
     * @param loginString
     * @param password
     * @return
     */
    public Result login(String loginString, String password) {
        // 根据输入的字符串类型获取对应user
        User user = getUserByName(loginString);
        // 查询出的user为空
        if (user == null) {
            return Result.info(ResultCode.USERNAME_EMAIL_NOT_EXIST);
        }
        // 密码不正确
        if (!CodingUtil.md5(password + user.getSalt()).equals(user.getPassword())) {
            return Result.info(ResultCode.PASSWORD_WRONG);
        }
        System.out.println(user.getStatus());
        // 账户未激活
        if (user.getStatus() == 0) {
            return Result.info(ResultCode.NOT_AUTH_SUCCESS).put("loginName",user.getLoginName())
                    .put("email",user.getEmail());
        }
        // 插入对应user的ticket记录
        // 将ticket和user的信息放到Result中返回给Controller
        return Result.info(ResultCode.LOGIN_SUCCESS)
                .put("ticket",loginTicketService.addLoginTicket(user.getId()))
                .put("user",removeSensitiveFields(user));
    }

    /**
     * 传递进来的user对象只有登录用户名，密码，邮箱有值。其余均为null
     * @param user
     * @return
     */
    public Result register(User user) {
        if (userDao.selectByLoginName(user.getLoginName()) != null || userDao.selectByEmail(user.getEmail()) != null) {
            return Result.info(ResultCode.USERNAME_EMAIL_REGISTERED);
        }
        // 默认昵称
        user.setNickName("我是一把格子伞");
        // 默认性别
        user.setGender("蒙面侠");
        // 默认随机头像
        user.setAvatar(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        // 8位盐值
        user.setSalt(UUID.randomUUID().toString().substring(0,8));
        // 带盐加密
        user.setPassword(CodingUtil.md5(user.getPassword() + user.getSalt()));
        // 设置禁用
        user.setStatus(0);
        // 生成邮箱验证token
        String token = UUID.randomUUID().toString().replaceAll("-","");
        user.setToken(token);
        // 设置邮箱验证token过期时间
        Date date = new Date();
        // 设置过期时间为24个小时，单位毫秒。
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        user.setExpired(date);
        // 执行注册（数据插入）返回的是影响的记录行数，而mybatis已经将Java的user对象中的id属性设置为新增记录的主键id了。
        if (userDao.insert(user) != 1) {
            throw new EntityUpdateException("异常：用户注册异常");
        }
        // 进行邮箱激活操作。。。
        eventProducer.fireEvent(new EventModel(EventType.RegisterMAIL).setExt("userId",CodingUtil.base64Encode(String.valueOf(user.getId())))
                .setExt("token",token).setExt("email",user.getEmail()));
        /**
         * ......
         */
        return Result.info(ResultCode.REGISTER_SUCCESS).put("loginName",user.getLoginName())
                .put("email",user.getEmail());
    }

    /**
     * 退出登录，即让ticket失效。
     * @param ticket
     * @return 返回影响的记录行数
     */
    public int logout(String ticket) {
        return loginTicketService.invalidate(ticket);
    }

    /**
     * 提取公共的方法：利用正则校验传入的name字符串格式，如果是邮箱格式则根据邮箱查询user，否则根据loginName查询user。
     * @param name
     * @return
     */
    public User getUserByName(String name) {
        // 邮箱正则
        String regex = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
        User user;
        if (name.matches(regex)) {
            // 使用邮箱登录
            user = userDao.selectByEmail(name);
        } else {
            // 使用用户名登录
            user = userDao.selectByLoginName(name);
        }
        return user;
    }

    /**
     * 提取公共的去除user的敏感字段的方法：去除password和salt字段
     * @param user
     * @return
     */
    public User removeSensitiveFields(User user) {
        user.setPassword(null);
        user.setSalt(null);
        return user;
    }

    /**
     * 注册邮箱激活服务
     * @param userId
     * @param token
     * @return
     */
    public int auth(int userId, String token) {
        User user = userDao.selectById(userId);
        // 未注册或token已过期或已激活
        if (user == null || user.getExpired().before(new Date()) || user.getStatus() != 0) {
            return 0;
        }
        return userDao.auth(userId,token);
    }
}