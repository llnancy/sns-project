package org.lilu.sns.async;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 事件模型。保存“事件现场”信息。所有的setter方法都返回当前对象，便于链式调用。
 */
public class EventModel implements Serializable {
    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 事件触发者id
     */
    private int actorId;

    /**
     * 事件载体id
     */
    private int entityId;

    /**
     * 事件载体类型
     */
    private int entityType;

    /**
     * 事件接收者id
     */
    private int ownerId;

    /**
     * 事件附加扩展信息
     */
    private Map<String,String> exts = new HashMap<>();

    /**
     * 无参构造函数
     */
    public EventModel() {

    }

    /**
     * 带eventType参构造函数
     * @param eventType
     */
    public EventModel(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * 根据key读取附加扩展信息的方法
     * @param key
     * @return
     */
    public String getExt(String key) {
        return exts.get(key);
    }

    /**
     * 传入key和value设置事件的附加扩展信息
     * @param key
     * @param value
     */
    public EventModel setExt(String key,String value) {
        exts.put(key,value);
        return this;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public EventModel setOwnerId(int ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "eventType=" + eventType +
                ", actorId=" + actorId +
                ", entityId=" + entityId +
                ", entityType=" + entityType +
                ", ownerId=" + ownerId +
                ", exts=" + exts +
                '}';
    }
}