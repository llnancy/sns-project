package org.lilu.sns.service;

import org.lilu.sns.util.JedisAdapter;
import org.lilu.sns.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Auther: lilu
 * @Date: 2019/3/6
 * @Description:
 */
@Service
public class FollowService {
    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 关注服务：用户可以关注某个实体，可以关注问题，用户等。
     * @param userId 用户id
     * @param entityId 实体id
     * @param entityType 实体类型
     * @return
     */
    public boolean follow(int userId,int entityId,int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        // 获取jedis连接对象
        Jedis jedis = jedisAdapter.getJedis();
        // 开启redis事务
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体粉丝数加1
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        // 用户关注实体对象数加1
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        // 提交事务
        List<Object> result = jedisAdapter.exec(tx,jedis);
        return validExecResult(result);
    }

    /**
     * 取消关注服务：对已关注的实体取消关注
     * @param userId
     * @param entityId
     * @param entityType
     * @return
     */
    public boolean unfollow(int userId,int entityId,int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        // 获取jedis连接对象
        Jedis jedis = jedisAdapter.getJedis();
        // 开启redis事务
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体粉丝数减1
        tx.zrem(followerKey,String.valueOf(userId));
        // 用户关注实体对象数减1
        tx.zrem(followeeKey,String.valueOf(entityId));
        // 提交事务
        List<Object> result = jedisAdapter.exec(tx,jedis);
        return validExecResult(result);
    }

    /**
     * 抽取共用方法：检查redis事务提交执行的返回值
     * @param result
     * @return
     */
    private boolean validExecResult(List<Object> result) {
        return result.size() == 2 && (Long) result.get(0) > 0 && (Long) result.get(1) > 0;
    }

    /**
     * 获取粉丝id列表
     * @param entityId 实体id
     * @param entityType 实体类型
     * @param count 查询数量
     * @return
     */
    public List<Integer> getFollowers(int entityId,int entityType,int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }

    /**
     * 获取关注对象id列表
     * @param userId 用户id
     * @param entityType 实体类型
     * @param count 查询数量
     * @return
     */
    public List<Integer> getFollowees(int userId,int entityType,int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    /**
     * 将Set集合转成List集合
     * @param idset
     * @return 返回ArrayList集合
     */
    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.valueOf(str));
        }
        return ids;
    }

    /**
     * 获取粉丝数量
     * @param entityId 实体id
     * @param entityType 实体类型
     * @return 返回粉丝数量
     */
    public long getFollowerCount(int entityId,int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return jedisAdapter.zcard(followerKey);
    }

    /**
     * 获取用户关注的实体数量
     * @param userId 用户id
     * @param entityType 实体类型
     * @return
     */
    public long getFolloweeCount(int userId,int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    /**
     * 判断用户是否关注了某个实体：用户是否为该实体粉丝
     * @param userId 用户id
     * @param entityId 实体id
     * @param entityType 实体类型
     * @return 关注了该实体则返回true，否则返回false
     */
    public boolean isFollower(int userId,int entityId,int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityId,entityType);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId)) != null;
    }
}
