package org.lilu.sns.async;

import com.alibaba.fastjson.JSON;
import org.lilu.sns.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 事件的消费者。从队列中取出事件进行处理。
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    /**
     * 日志工具
     */
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    /**
     * 事件的配置信息：事件类型，需要哪些事件处理器处理。
     */
    private Map<EventType, List<EventHandler>> eventConfig = new HashMap<>();

    /**
     * spring上下文
     */
    private ApplicationContext applicationContext;

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * 实现InitializingBean接口初始化bean。
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 通过spring上下文获取所有EventHandler类型的bean对象
        Map<String,EventHandler> eventHandlerBeans = applicationContext.getBeansOfType(EventHandler.class);
        // 配置事件信息
        if (eventHandlerBeans != null) {
            // 遍历EventHandler事件处理器的map集合。
            for (Map.Entry<String,EventHandler> entry : eventHandlerBeans.entrySet()) {
                // 获取事件处理器
                EventHandler eventHandler = entry.getValue();
                // 获取事件处理器支持的事件类型。
                List<EventType> eventTypes = eventHandler.getSupportEventTypes();
                // 遍历事件类型集合。
                for (EventType eventType : eventTypes) {
                    // 如果事件配置中不存在该事件类型，则新建一个该类型的配置。
                    if (!eventConfig.containsKey(eventType)) {
                        eventConfig.put(eventType,new ArrayList<>());
                    }
                    // 然后直接将事件类型对应的事件处理器添加到事件配置信息中。
                    eventConfig.get(eventType).add(eventHandler);
                }
            }
        }
        // 开启线程池处理事件
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute( () -> {
            while (true) {
                String eventQueueKey = RedisKeyUtil.getEventQueueKey();
                // 从list消息队列中弹出元素
                List<String> results = jedisCluster.brpop(0,eventQueueKey);
                for (String result : results) {
                    // 由于redis中brpop命令返回的第一个元素是弹出元素所在键的名称，第二个元素是弹出元素的值。需要过滤键。
                    if (result.equals(eventQueueKey)) {
                        // 结束当前循环，进行下一次循环。
                        continue;
                    }
                    // 将从队列中弹出的元素值反序列化成EventModel对象。
                    EventModel eventModel = JSON.parseObject(result,EventModel.class);
                    if (!eventConfig.containsKey(eventModel.getEventType())) {
                        logger.error("从队列中获取了不能识别的事件");
                        continue;
                    }
                    for (EventHandler eventHandler : eventConfig.get(eventModel.getEventType())) {
                        eventHandler.doEvent(eventModel);
                    }
                }
            }
        });
    }

    /**
     * 实现ApplicationContextAware接口设置spring上下文。
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}