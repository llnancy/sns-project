package org.lilu.sns.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.LoginTicketService;
import org.lilu.sns.service.UserService;
import org.lilu.sns.util.AppUtil;
import org.lilu.sns.util.CodingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description:
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        // 获取并遍历cookie，找到名为ticket的cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + ":" + cookie.getValue());
                if ("ticket".equals(cookie.getName())) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if (ticket != null) {
            LoginTicket loginTicket = loginTicketService.getLoginTicket(ticket);
            if (loginTicket != null) {
                // 校验ticket的有效性。
                if (!loginTicketService.checkValidity(loginTicket)) {
                    // ticket无效，且访问的接口是登录接口，则通过拦截器。
                    if ("/user/login".equals(request.getRequestURI())) {
                        return true;
                    }
                    // ticket无效，且访问的接口非登录接口，则登录已过期，直接向客户端返回json，并返回false拦截该次请求。
                    AppUtil.returnJson(response,JSONObject.toJSONString(Result.info(ResultCode.LOGIN_EXPIRED)));
                    return false;
                }
                // 获取有效的ticket对应的user
                User ticketUser = userService.selectUserById(loginTicket.getUserId());
                // ticket有效，拦截重复的登录。
                if ("/user/login".equals(request.getRequestURI())) {
                    // 如果ticket对应的用户与请求中要登录用户相同，则表示该用户已经登录，不必重复登录。
                    String loginString = request.getParameter("loginString");
                    String password = request.getParameter("password");
                    User loginUser = userService.getUserByName(loginString);
                    // 重写了User类的equals方法
                    if (ticketUser != null && ticketUser.equals(loginUser)
                            && ticketUser.getPassword().equals(CodingUtil.md5(password + ticketUser.getSalt()))
                            && loginUser.getPassword().equals(CodingUtil.md5(password + loginUser.getSalt()))) {
                        // 当ticketUser与loginUser相同时表示已经登录，需要拦截请求。否则正常调用登录接口。
                        AppUtil.returnJson(response,JSONObject.toJSONString(Result.info(ResultCode.LOGGED_IN)));
                        return false;
                    }
                }
                // ticket有效：将ticket对应的user放入与线程绑定的hostHolder对象中。
                hostHolder.setUser(ticketUser);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("拦截器完成：" + hostHolder.getUser());
        hostHolder.clear();
    }
}