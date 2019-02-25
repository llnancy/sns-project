package org.lilu.sns.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: lilu
 * @Date: 2019/1/27
 * @Description:
 */
public class AppUtil {
    private static final Logger logger = LoggerFactory.getLogger(CodingUtil.class);

    /**
     * 系统管理员id，用来发送系统站内信和消息。
     */
    public static int SYSTEM_ADMINID = 0;

    /**
     * 用于在拦截器中向客户端返回json数据
     * @param response
     * @param json
     */
    public static void returnJson(HttpServletResponse response,String json) {
        PrintWriter out = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        // 设置跨域
        response.setHeader("Access-Control-Allow-Origin","http://127.0.0.1:8080");
        response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Credentials","true");
        try {
            out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (IOException e) {
            logger.error("returnJson error : ",e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}