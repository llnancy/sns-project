package org.lilu.sns.async;

import com.alibaba.fastjson.JSONObject;
import org.lilu.sns.util.JedisAdapter;
import org.lilu.sns.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 事件的生产者。将事件推送到队列中。
 */
@Service
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 推送事件至队列。使用redis的List实现的队列。
     * @param eventModel 事件模型对象
     * @return
     */
    public boolean fireEvent(EventModel eventModel) {
        try {
            jedisAdapter.lpush(RedisKeyUtil.getEventQueueKey(),JSONObject.toJSONString(eventModel));
            return true;
        } catch (Exception e) {
            logger.error("异步事件推送至队列失败：" + e.getMessage());
            return false;
        }
    }
}