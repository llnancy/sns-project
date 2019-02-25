package org.lilu.sns.service;

import org.lilu.sns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * @Auther: lilu
 * @Date: 2019/2/20
 * @Description: 点赞点踩业务
 */
@Service
public class LikeService {
     @Autowired
     private JedisCluster jedisCluster;

    /**
     * 点赞业务。返回已点赞的数量。
     * @param userId
     * @param entityId
     * @param entityType
     * @return
     */
    public Long like(int userId,int entityId,int entityType,int hasLiked) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        // 如果已经点赞，则取消点赞；如果还未点赞或者已经点踩，则进行点赞。
        if (hasLiked == 1) {
            // 已经点赞则取消点赞
            jedisCluster.srem(likeKey,String.valueOf(userId));
        } else if (hasLiked == 0) {
            // 还未点赞和点踩则进行点赞
            jedisCluster.sadd(likeKey,String.valueOf(userId));
        } else if (hasLiked == -1) {
            // 已经点踩则进行点赞并移出点踩集合
            jedisCluster.sadd(likeKey,String.valueOf(userId));
            // 从dislike集合中删除
            jedisCluster.srem(RedisKeyUtil.getDislikeKey(entityId,entityType),String.valueOf(userId));
        }
        // 返回like集合的元素个数
        return jedisCluster.scard(likeKey);
    }

    /**
     * 点踩业务。返回已点踩的数量。
     * @param userId
     * @param entityId
     * @param entityType
     * @return
     */
    public Long dislike(int userId,int entityId,int entityType,int hasDisliked) {
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        // 如果已经点踩，则取消点踩；如果还未点踩或已经点赞，则进行点踩。
        if (hasDisliked == -1) {
            // 已经点踩则取消点踩
            jedisCluster.srem(dislikeKey,String.valueOf(userId));
        } else if (hasDisliked == 0) {
            // 还未点赞和点踩则进行点踩
            jedisCluster.sadd(dislikeKey,String.valueOf(userId));
        } else if (hasDisliked == 1) {
            // 已经点赞则进行点踩并移出点赞集合
            jedisCluster.sadd(dislikeKey,String.valueOf(userId));
            jedisCluster.srem(likeKey,String.valueOf(userId));
        }
        return jedisCluster.scard(likeKey);
    }

    /**
     * 获取userId对某个entity的点赞状态。
     * 如果已点赞，返回1；
     * 如果已点踩，返回-1；
     * 如果没有赞踩，返回0。
     * @param userId
     * @param entityId
     * @param entityType
     * @return
     */
    public int getLikeStatus(int userId,int entityId,int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        // 已点赞返回1
        if (jedisCluster.sismember(likeKey,String.valueOf(userId))) {
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);
        // 已点踩返回-1，否则返回0。
        return jedisCluster.sismember(dislikeKey,String.valueOf(userId)) ? -1 : 0;
    }

    /**
     * 获取已点赞集合中元素的数量。
     * @param entityId
     * @param entityType
     * @return
     */
    public long getLikeCount(int entityId,int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        return jedisCluster.scard(likeKey);
    }
}