package org.lilu.sns.service;

import org.lilu.sns.util.JedisAdapter;
import org.lilu.sns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: lilu
 * @Date: 2019/2/20
 * @Description: 点赞点踩业务
 */
@Service
public class LikeService {
     @Autowired
     private JedisAdapter jedisAdapter;

    /**
     * 点赞业务。返回已点赞的数量。
     * @param userId 当前登录用户id
     * @param entityId 点赞对象实体id
     * @param entityType 点赞对象实体类型
     * @return
     */
    public Long like(int userId,int entityId,int entityType,int hasLiked) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        // 如果已经点赞，则取消点赞；如果还未点赞或者已经点踩，则进行点赞。
        if (hasLiked == 1) {
            // 已经点赞则取消点赞
            jedisAdapter.srem(likeKey,String.valueOf(userId));
        } else if (hasLiked == 0) {
            // 还未点赞和点踩则进行点赞
            jedisAdapter.sadd(likeKey,String.valueOf(userId));
        } else if (hasLiked == -1) {
            // 已经点踩则进行点赞并移出点踩集合
            jedisAdapter.sadd(likeKey,String.valueOf(userId));
            // 从dislike集合中删除
            jedisAdapter.srem(RedisKeyUtil.getDislikeKey(entityId,entityType),String.valueOf(userId));
        }
        // 返回like集合的元素个数
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 点踩业务。返回已点踩的数量。
     * @param userId 当前登录用户id
     * @param entityId 点赞对象实体id
     * @param entityType 点赞对象实体类型
     * @return
     */
    public Long dislike(int userId,int entityId,int entityType,int hasDisliked) {
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        // 如果已经点踩，则取消点踩；如果还未点踩或已经点赞，则进行点踩。
        if (hasDisliked == -1) {
            // 已经点踩则取消点踩
            jedisAdapter.srem(dislikeKey,String.valueOf(userId));
        } else if (hasDisliked == 0) {
            // 还未点赞和点踩则进行点踩
            jedisAdapter.sadd(dislikeKey,String.valueOf(userId));
        } else if (hasDisliked == 1) {
            // 已经点赞则进行点踩并移出点赞集合
            jedisAdapter.sadd(dislikeKey,String.valueOf(userId));
            jedisAdapter.srem(likeKey,String.valueOf(userId));
        }
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 获取userId对某个entity的点赞状态。
     * 如果已点赞，返回1；
     * 如果已点踩，返回-1；
     * 如果没有赞踩，返回0。
     * @param userId 当前登录用户id
     * @param entityId 点赞对象实体id
     * @param entityType 点赞对象实体类型
     * @return
     */
    public int getLikeStatus(int userId,int entityId,int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        // 已点赞返回1
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId))) {
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityId,entityType);
        // 已点踩返回-1，否则返回0。
        return jedisAdapter.sismember(dislikeKey,String.valueOf(userId)) ? -1 : 0;
    }

    /**
     * 获取已点赞集合中元素的数量。
     * @param entityId 点赞对象实体id
     * @param entityType 点赞对象实体类型
     * @return
     */
    public long getLikeCount(int entityId,int entityType) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        return jedisAdapter.scard(likeKey);
    }
}