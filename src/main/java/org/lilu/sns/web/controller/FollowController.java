package org.lilu.sns.web.controller;

import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventProducer;
import org.lilu.sns.async.EventType;
import org.lilu.sns.dao.QuestionDao;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.FollowService;
import org.lilu.sns.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/3/6
 * @Description: 关注服务控制中心
 */
@RestController
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private QuestionService questionService;

    /**
     * 关注用户
     * @param userId
     * @return
     */
    @GetMapping("/followUser")
    public Result followUser(@RequestParam("userId") int userId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        boolean result = followService.follow(user.getId(),userId,EntityType.ENTITY_USER);
        // 发送站内信
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(user.getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setOwnerId(userId));
        return result ? Result.success().put("followeeCount",followService.getFolloweeCount(user.getId(),EntityType.ENTITY_USER)) : Result.fail();
    }

    /**
     * 取消关注用户
     * @param userId
     * @return
     */
    @GetMapping("/unfollowUser")
    public Result unfollowUser(@RequestParam("userId") int userId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        boolean result = followService.unfollow(user.getId(),userId,EntityType.ENTITY_USER);
        // 发送站内信
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(user.getId())
                .setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setOwnerId(userId));
        return result ? Result.success().put("followeeCount",followService.getFolloweeCount(user.getId(),EntityType.ENTITY_USER)) : Result.fail();
    }

    /**
     * 根据参数hasFollowed决定是关注问题还是取消关注
     * @param questionId 问题id
     * @param hasFollowed 是否关注
     * @return 关注信息
     */
    @GetMapping("/followQuestion")
    public Result followQuestion(@RequestParam("questionId") int questionId,@RequestParam("hasFollowed") int hasFollowed) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        Question question = questionService.selectQuestionByQuestionId(questionId);
        if (question == null) {
            return Result.info(ResultCode.PARAMETER_NOT_VALID);
        }
        boolean result;
        if (hasFollowed == 0) {
            // 等于0：未关注，进行关注。
            result = followService.follow(user.getId(),questionId,EntityType.ENTITY_QUESTION);
            // 发送站内信通知
            eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(user.getId())
                    .setEntityId(questionId)
                    .setEntityType(EntityType.ENTITY_QUESTION)
                    .setOwnerId(question.getUserId()));
        } else if (hasFollowed == 1) {
            // 等于1：已关注，取消关注。
            result = followService.unfollow(user.getId(),questionId,EntityType.ENTITY_QUESTION);
        } else {
            // 错误参数
            return Result.info(ResultCode.PARAMETER_NOT_VALID);
        }
        // 获取关注该问题的最新用户
        List<User> users = questionService.getFollowersUser(questionId);
        // 获取当前登录用户对该问题的登录状态
        int followStatus = followService.isFollower(user.getId(),questionId,EntityType.ENTITY_QUESTION) ? 1 : 0;
        return result ? Result.success().put("follow_status",followStatus)
                .put("follower_count",followService.getFollowerCount(questionId,EntityType.ENTITY_QUESTION))
                .put("follower_users",users): Result.fail();
    }
}
