package org.lilu.sns.async.handler;

import org.lilu.sns.async.EventHandler;
import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventType;
import org.lilu.sns.pojo.Message;
import org.lilu.sns.pojo.User;
import org.lilu.sns.service.MessageService;
import org.lilu.sns.service.UserService;
import org.lilu.sns.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 点赞事件处理器。
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    /**
     * 点赞事件发生，给被点赞用户发送一条站内信。
     * @param eventModel
     */
    @Override
    public void doEvent(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(AppUtil.SYSTEM_ADMINID);
        message.setToId(eventModel.getOwnerId());
        message.setCreatedDate(new Date());
        message.setHasRead(1);
        User user = userService.selectUserById(eventModel.getActorId());
        message.setContent("用户 " + user.getLoginName() + "（"
                + user.getEmail() + "）赞了你的评论，http://"
                + environment.getProperty("server.address") + ":"
                + environment.getProperty("server.port")
                + "/question/" + eventModel.getExt("questionId"));
        messageService.insertMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}