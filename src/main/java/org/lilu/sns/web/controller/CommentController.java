package org.lilu.sns.web.controller;

import org.hibernate.validator.constraints.Length;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.CommentService;
import org.lilu.sns.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Auther: lilu
 * @Date: 2019/1/29
 * @Description: 评论中心控制器
 */
@RestController
@Validated
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private QuestionService questionService;

    /**
     * 添加一条评论
     * @param questionId
     * @param content
     * @return
     */
    @PostMapping
    @Transactional
    public Result addComment(@RequestParam("questionId") Integer questionId,
                             @Length(min = 1,max = 200,message = "评论字数范围为1~200")
                             @RequestParam("content") String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setContent(content);
        comment.setCreatedDate(new Date());
        comment.setEntityId(questionId);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setStatus(0);
        if (commentService.insertComment(comment) != 1) {
            throw new EntityUpdateException("异常：评论发表失败");
        }
        // 修改对应question的commentCount字段，如果在此处修改，则需要控制事务。
        int count = commentService.selectCommentCount(comment.getEntityId(),comment.getEntityType());
        if (questionService.updateCommentCount(comment.getEntityId(),count) != 1) {
            throw new EntityUpdateException("异常：更新评论数量失败");
        }
        return Result.info(ResultCode.COMMENT_ADD_SUCCESS);
    }
}