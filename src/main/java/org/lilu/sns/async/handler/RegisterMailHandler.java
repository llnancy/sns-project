package org.lilu.sns.async.handler;

import org.lilu.sns.async.EventHandler;
import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventType;
import org.lilu.sns.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/2/25
 * @Description: 注册发送邮件处理器
 */
@Component
public class RegisterMailHandler implements EventHandler {
    @Autowired
    private MailService mailService;

    @Value("${front.url}")
    private String url;

    @Override
    public void doEvent(EventModel eventModel) {
        Map<String,Object> data = new HashMap<>();
        data.put("url",url);
        data.put("login_email",eventModel.getExt("email"));
        data.put("userId",eventModel.getExt("userId"));
        data.put("token",eventModel.getExt("token"));
        mailService.sendMailWithHtmlTemplate(eventModel.getExt("email"),"《格子伞社区》注册激活","mails/login_auth.tpl",data);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.RegisterMAIL);
    }
}
