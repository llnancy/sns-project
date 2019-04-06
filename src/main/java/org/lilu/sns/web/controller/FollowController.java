package org.lilu.sns.web.controller;

import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventProducer;
import org.lilu.sns.async.EventType;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.FollowService;
import org.lilu.sns.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 关注/取关 用户
     * @param userId 关注/取关 用户对象的id
     * @param hasFollowed 当前登录用户对userId对应用户的关注状态
     * @return 返回关注/取关对象用户最新粉丝数和当前登录用户对userId对应用户的关注状态
     */
    @GetMapping("/followUser")
    public Result followUser(@RequestParam("userId") int userId,@RequestParam("hasFollowed") int hasFollowed) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        boolean result;
        if (hasFollowed == 0) {
            // 等于0：未关注，进行关注。
            result = followService.follow(user.getId(),userId,EntityType.ENTITY_USER);
            // 发送站内信通知
            eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(user.getId())
                    .setEntityId(userId)
                    .setEntityType(EntityType.ENTITY_USER)
                    .setOwnerId(userId));
        } else if (hasFollowed == 1) {
            // 等于1：已关注，取消关注。
            result = followService.unfollow(user.getId(),userId,EntityType.ENTITY_USER);
        } else {
            // 错误参数
            return Result.info(ResultCode.PARAMETER_NOT_VALID);
        }
        return result ? Result.success().put("user_follow_count",followService.getFollowerCount(userId,EntityType.ENTITY_USER))
                .put("follow_status",followService.isFollower(user.getId(),userId,EntityType.ENTITY_USER)) : Result.fail();
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
        // 获取当前登录用户对该问题的登录状态
        int followStatus = followService.isFollower(user.getId(),questionId,EntityType.ENTITY_QUESTION);
        return result ? Result.success().put("follow_status",followStatus)
                .put("follower_count",followService.getFollowerCount(questionId,EntityType.ENTITY_QUESTION))
                // 获取关注该问题的最新用户
                .put("follower_users",questionService.getFollowersUser(questionId)): Result.fail();
    }

    @GetMapping("/getLoginUserFollowUserStatus")
    public Result getLoginUserFollowUserStatus(@RequestParam("userId") int userId) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        int followUserStatus = followService.isFollower(user.getId(),userId,EntityType.ENTITY_USER);
        return Result.success().put("follow_status",followUserStatus);
    }
}
