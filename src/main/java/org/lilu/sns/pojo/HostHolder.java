package org.lilu.sns.pojo;

import org.springframework.stereotype.Component;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description: 与线程绑定，每一个请求对应一个user
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public User getUser() {
        return userThreadLocal.get();
    }

    public void setUser(User user) {
        userThreadLocal.set(user);
    }

    public void clear() {
        userThreadLocal.remove();
    }
}