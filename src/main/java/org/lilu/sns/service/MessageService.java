package org.lilu.sns.service;

import org.lilu.sns.dao.MessageDao;
import org.lilu.sns.pojo.Message;
import org.lilu.sns.pojo.User;
import org.lilu.sns.pojo.ViewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/30
 * @Description: 消息中心业务层
 */
@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SensitiveService sensitiveService;

    /**
     * 查询一个会话的所有消息
     * @param conversationId
     * @return
     */
    public List<ViewObject> selectConversationDetail(String conversationId,int localUserId) {
        messageDao.updateMessageReadStatus(localUserId,conversationId);
        List<Message> messages = messageDao.selectConversationDetail(conversationId);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : messages) {
            ViewObject vo = getMessageDetail(message);
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 获取localUserId的所有会话
     * @param localUserId
     * @return
     */
    public List<ViewObject> selectConversationList(int localUserId) {
        List<Message> messages = messageDao.selectConversationList(localUserId);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : messages) {
            ViewObject vo = getMessageDetail(message, localUserId);
            // 添加未读消息数量
            vo.set("unread",messageDao.selectConversationUnreadCount(localUserId,message.getConversationId()));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 发送一条消息
     * @param message
     * @return
     */
    public int insertMessage(Message message) {
        // 过滤HTML标签
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        // 过滤消息内容中的敏感词
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.insert(message);
    }

    /**
     * 传入一个message，返回一个包含message、user、isSend的ViewObject
     * user：当前登录user是私信的发送者则为私信的接收者；
     *       当前登录user是私信的接收者则为私信的发送者。
     * isSend：标记这条message是否是当前登录用户发送出去的，便于前端显示。
     *
     * 会话列表页面：
     * 1、当前登录用户是这条私信的发送者：
     * 则页面上应该显示这条私信的接收者的头像信息，
     * 文字内容是：我发送给 接收者 ：私信内容
     *
     * 2、当前登录用户是这条私信的接收者：
     * 则页面上应该显示这条私信的发送者的头像信息，
     * 文字内容是：发送者：私信内容
     *
     * 总结：“我”是私信的发送者则显示私信接收者头像，
     * “我”是私信的接收者则显示私信发送者头像。
     *
     * @param message
     * @param localUserId
     * @return
     */
    private ViewObject getMessageDetail(Message message, int localUserId) {
        ViewObject vo = new ViewObject();
        if (message != null) {
            User user = null;
            boolean isSend = false;
            if (localUserId == message.getFromId()) {
                user = userService.selectUserById(message.getToId());
                // 这条message是当前登录user发送出去的
                isSend = true;
            } else if (localUserId == message.getToId()) {
                user = userService.selectUserById(message.getFromId());
            }
            vo.set("message",message);
            vo.set("user",userService.removeSensitiveFields(user));
            vo.set("isSend",isSend);
        }
        return vo;
    }

    /**
     * 重载的方法：传入一个message，返回一个包含message、user的ViewObject
     * user：消息的发送者
     * @param message
     * @return
     */
    private ViewObject getMessageDetail(Message message) {
        ViewObject vo = new ViewObject();
        if (message != null) {
            User user = userService.selectUserById(message.getFromId());
            vo.set("message",message);
            vo.set("user",userService.removeSensitiveFields(user));
        }
        return vo;
    }
}