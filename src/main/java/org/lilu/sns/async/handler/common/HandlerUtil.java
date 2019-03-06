package org.lilu.sns.async.handler.common;

import org.lilu.sns.async.EventModel;
import org.lilu.sns.pojo.Message;
import org.lilu.sns.util.AppUtil;

import java.util.Date;

/**
 * @Auther: lilu
 * @Date: 2019/3/6
 * @Description: handler工具类
 */
public class HandlerUtil {
    /**
     * 构建基本消息内容
     * @param eventModel 传入事件模型
     * @return 返回构建好的消息
     */
    public static Message buildBaseMessage(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(AppUtil.SYSTEM_ADMINID);
        message.setToId(eventModel.getOwnerId());
        message.setCreatedDate(new Date());
        message.setHasRead(1);
        return message;
    }
}
