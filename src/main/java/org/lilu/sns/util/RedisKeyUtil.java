package org.lilu.sns.util;

/**
 * @Auther: lilu
 * @Date: 2019/2/20
 * @Description: 生成redis的key的工具类
 */
public class RedisKeyUtil {
    /**
     * 分隔符
     */
    private static final String SPLIT = ":";

    /**
     * 点赞业务
     */
    private static final String BIZ_LIKE = "LIKE";

    /**
     * 点踩业务
     */
    private static final String BIZ_DISLIKE = "DISLIKE";

    /**
     * 事件队列：有序集合实现
     */
    private static final String BIZ_EVENTQUEUE = "EVENT_QUEUE";

    /**
     * 生成点赞的key
     * @param entityId
     * @param entityType
     * @return
     */
    public static String getLikeKey(int entityId,int entityType) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成点踩的key
     * @param entityId
     * @param entityType
     * @return
     */
    public static String getDislikeKey(int entityId,int entityType) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成事件队列的key
     * @return
     */
    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }
}