package org.lilu.sns.async;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 事件类型
 */
public enum EventType {
    LIKE("LIKE"),
    COMMENT("COMMENT"),
    RegisterMAIL("RegisterMAIL"),
    FOLLOW("FOLLOW"),
    UNFOLLOW("UNFOLLOW")
    ;
    private String eventName;
    EventType(String eventName) {
        this.eventName = eventName;
    }
    public String getEventName() {
        return eventName;
    }
}