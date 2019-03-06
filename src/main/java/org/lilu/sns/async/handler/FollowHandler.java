package org.lilu.sns.async.handler;

import org.lilu.sns.async.EventHandler;
import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventType;
import org.lilu.sns.async.handler.common.HandlerUtil;
import org.lilu.sns.pojo.EntityType;
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
 * @Date: 2019/3/6
 * @Description: 关注服务处理器
 */
@Component
public class FollowHandler implements EventHandler {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    /**
     * 关注服务系统站内信通知
     * @param eventModel
     */
    @Override
    public void doEvent(EventModel eventModel) {
        Message message = HandlerUtil.buildBaseMessage(eventModel);
        User user = userService.selectUserById(eventModel.getActorId());
        if (eventModel.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户 " + user.getLoginName() + "（"
                    + user.getEmail() + "）关注了你的问题，http://"
                    + environment.getProperty("server.address") + ":"
                    + environment.getProperty("server.port")
                    + "/question/" + eventModel.getEntityId());
        } else if (eventModel.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户 " + user.getLoginName() + "（"
                    + user.getEmail() + "）关注了你，http://"
                    + environment.getProperty("server.address") + ":"
                    + environment.getProperty("server.port")
                    + "/user/" + eventModel.getActorId());
        }
        messageService.insertMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
