package org.lilu.sns.web.controller;

import org.hibernate.validator.constraints.Length;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.pojo.Result;
import org.lilu.sns.pojo.ResultCode;
import org.lilu.sns.pojo.User;
import org.lilu.sns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description: 登录/注册功能控制器
 */
@RestController
@Validated
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param loginString
     * @param password 登录时不需要校验密码长度
     * @param response
     * @return
     */
    @PostMapping("/login")
    public Result login(@Length(min = 2,max = 32,message = "用户名或邮箱有误")
                            @RequestParam("loginString") String loginString,
                        @RequestParam("password") String password,
                        HttpServletResponse response) {
        Result result = userService.login(loginString,password);
        Map<String,Object> data = result.getData();
        // 登录成功，发送cookie
        if (data.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket",String.valueOf(data.get("ticket")));
            cookie.setPath("/");
            // 下发cookie到浏览器客户端
            response.addCookie(cookie);
            // 删除响应信息中ticket内容
            data.remove("ticket");
        }
        return result;
    }

    /**
     * 注册
     * @param user 若校验失败，则抛出异常，由MyExceptionHandler类进行全局的异常处理。
     * @param result 必须要写该参数，否则不会抛出ConstraintViolationException异常。
     * @return
     */
    @PostMapping("/register")
    public Result register(@Valid User user, BindingResult result) {
//        if (result.hasErrors()) {
//            // 服务端校验数据
//            Map<String,Object> errors = new HashMap<>();
//            List<FieldError> fieldErrors = result.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                logger.error("注册异常-->" + fieldError.getField() + " : " + fieldError.getDefaultMessage());
//                errors.put(fieldError.getField(),fieldError.getDefaultMessage());
//            }
//            return Result.info(ResultCode.REGISTER_FAIL).put("fieldErrors",errors);
//        } else {
//            return userService.register(user);
//        }

        return userService.register(user);
    }

    /**
     * 退出登录
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public Result logout(@CookieValue("ticket") String ticket) {
        if (userService.logout(ticket) != 1) {
            throw new EntityUpdateException("异常：退出登录失败");
        }
        return Result.info(ResultCode.LOGOUT_SUCCESS);
    }
}