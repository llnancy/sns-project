package org.lilu.sns.async;

import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 事件处理管理器。
 */
public interface EventHandler {
    /**
     * 处理事件的方法。
     * @param eventModel
     */
    void doEvent(EventModel eventModel);

    List<EventType> getSupportEventTypes();
}