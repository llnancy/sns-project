package org.lilu.sns.web.controller;

import org.hibernate.validator.constraints.Length;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.exception.SystemErrorException;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.MessageService;
import org.lilu.sns.service.UserService;
import org.lilu.sns.util.CodingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Auther: lilu
 * @Date: 2019/1/30
 * @Description: 消息中心控制器
 */
@RestController
@Validated
@RequestMapping("/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 给toName代表的user发送一条内容为content的消息
     * @param toName
     * @param content
     * @return
     */
    @PostMapping
    public Result addMessage(@RequestParam("toName") String toName,
                             @Length(min = 1,max = 255,message = "消息内容长度范围为：1~255")
                             @RequestParam("content") String content) {
        User fromUser = hostHolder.getUser();
        if (fromUser == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        User toUser = userService.getUserByName(toName);
        if (toUser == null) {
            return Result.info(ResultCode.USER_NOT_EXIST);
        }
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());
        message.setContent(content);
        message.setCreatedDate(new Date());
        // 默认1表示未读
        message.setHasRead(1);
        // conversation_id字段的值在Message实体类中的getConversationId方法中动态设置过了。
        if (messageService.insertMessage(message) != 1) {
            throw new EntityUpdateException("异常：消息发送失败");
        }
        return Result.info(ResultCode.MESSAGE_ADD_SUCCESS);
    }

    /**
     * 根据conversationId（会话id）获取会话的全部消息内容
     * @param conversationId
     * @return
     */
    @GetMapping("/conversation/{conversationId}")
    @Transactional
    public Result getConversationDetail(@PathVariable("conversationId") String conversationId) {
        if (hostHolder.getUser() == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        } else {
            // 尝试将conversationId进行base64解码，如果解码失败会自动抛出异常。
            // 在统一异常处理器中配置的对Exception异常的处理可以捕获到该处抛出的异常。考虑使用try-catch语句包裹？？？
            String _conversationId = CodingUtil.base64Decode(conversationId);
            // 特殊情况：MTFfMTg和MTFfMTg=这两个字符串解码后得到的结果一样，但只能是和数据库中保存的字符串一样才能查询到结果。
            System.out.println("解码：" + _conversationId);
            // 存储时是将消息发送者和接收者的id按由小到大的顺序以下划线_进行拼接的。
            // 可能出现的异常：用户随便输入一个字符串，恰好能被base64解码，
            // 解码之后的字符串型为：11_  这样split后的字符串数组就只有一个元素。
            // 这种情况下回出现ArrayIndexOutOfBoundsException异常
            String[] split = _conversationId.split("_");
            System.out.println("会话人：" + split[0]);
            System.out.println("会话人：" + split[1]);
            int localUserId = hostHolder.getUser().getId();
            // 获取当前登录user的id字符串
            String localUserIdStr = String.valueOf(localUserId);
            // 如果当前登录user的id和conversationId无关，则该会话不属于该登录user。
            if (!(split[0].equals(localUserIdStr) || split[1].equals(localUserIdStr))) {
                throw new SystemErrorException("试图获取不属于自己的会话");
            }
            // 获取相互发送私信的"对方"user，并加入到返回结果中。
            int toUserId = split[0].equals(localUserIdStr) ? Integer.parseInt(split[1]) : Integer.parseInt(split[0]);
            User toUser = userService.selectUserById(toUserId);
            return Result.success().put("messages_info",messageService.selectConversationDetail(conversationId,localUserId))
                    .put("toUser",userService.removeSensitiveFields(toUser));
        }
    }

    /**
     * 获取会话列表信息（包含会话的最新信息，会话的未读消息数，会话的发送者（与当前登录user进行对话的user）信息）
     * @return
     */
    @GetMapping("/conversation")
    public Result getConversationList() {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        } else {
            return Result.success().put("conversations_info",messageService.selectConversationList(user.getId()));
        }
    }
}