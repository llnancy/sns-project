package org.lilu.sns.web.controller;

import org.lilu.sns.async.EventModel;
import org.lilu.sns.async.EventProducer;
import org.lilu.sns.async.EventType;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.CommentService;
import org.lilu.sns.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: lilu
 * @Date: 2019/2/20
 * @Description: 点赞点踩控制中心
 */
@RestController
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    /**
     * 事件生产者
     */
    @Autowired
    private EventProducer eventProducer;

    /**
     * 进行点赞操作，返回已点赞的数量。
     * @param commentId
     * @return
     */
    @GetMapping("/commentLike/{commentId}")
    public Result like(@PathVariable("commentId") int commentId,@RequestParam("hasLiked") int hasLiked) {
        User loginUser = hostHolder.getUser();
        if (loginUser == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        Comment comment = commentService.selectCommentById(commentId);
        // 当前既未点赞又未点踩则发送点赞事件。
        // 用户可能发生多次点赞点踩，取消点赞点踩，这样就会导致发送多次站内信，如果用户无限恶意点击，如何防止？？
        if (hasLiked == 0) {
            // 生产点赞事件
            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(loginUser.getId())
                    .setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT)
                    .setOwnerId(comment.getUserId())
                    .setExt("questionId",String.valueOf(comment.getEntityId())));
        }
        return Result.success().put("like_count",likeService.like(loginUser.getId(),commentId,EntityType.ENTITY_COMMENT,hasLiked))
                .put("like_status",likeService.getLikeStatus(loginUser.getId(),commentId,EntityType.ENTITY_COMMENT));
    }

    /**
     * 进行点踩操作，仍然返回已点赞的数量。
     * @param commentId
     * @return
     */
    @GetMapping("/commentDislike/{commentId}")
    public Result dislike(@PathVariable("commentId") int commentId,@RequestParam("hasDisliked") int hasDisliked) {
        User loginUser = hostHolder.getUser();
        if (loginUser == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        return Result.success().put("like_count",likeService.dislike(loginUser.getId(),commentId,EntityType.ENTITY_COMMENT,hasDisliked))
                .put("like_status",likeService.getLikeStatus(loginUser.getId(),commentId,EntityType.ENTITY_COMMENT));
    }
}