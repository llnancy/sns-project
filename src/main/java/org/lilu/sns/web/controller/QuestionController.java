package org.lilu.sns.web.controller;

import org.hibernate.validator.constraints.Length;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/25
 * @Description: 问题中心控制器
 */
@RestController
@Validated
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    /**
     * 获取全部最新问题
     * @return
     */
    @RequestMapping({"/all"})
    public Result getLatestQuestions() {
        return Result.success().put("questions_info",questionService.selectLatestQuestions(0));
    }

    /**
     * 添加问题
     * @param title
     * @param content
     * @return
     */
    @PostMapping
    public Result addQuestion(@Length(min = 4,max = 64,message = "标题长度范围为4到64个字符")
                               @RequestParam("title") String title,
                              @Length(min = 4,max = 255,message = "问题长度范围为4到255个字符")
                               @RequestParam("content") String content) {
        User user = hostHolder.getUser();
        // 如果hostHolder中没有user则视为未登录状态
        if (user == null) {
            return Result.info(ResultCode.NOT_LOGGED_IN);
        }
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setCreatedDate(new Date());
        question.setCommentCount(0);
        question.setUserId(user.getId());
        if (questionService.insertQuestion(question) != 1) {
            throw new EntityUpdateException("异常：问题发布失败");
        }
        return Result.info(ResultCode.QUESTION_ADD_SUCCESS);
    }

    /**
     * 根据questionId获取问题详细信息（包含问题的评论详细信息，关注该问题的用户信息等）
     * @param questionId
     * @return
     */
    @RequestMapping("/{questionId}")
    public Result getQuestionById(@PathVariable("questionId") int questionId) {
        ViewObject question_info = questionService.selectQuestionById(questionId);
        if (question_info.get("question") == null) {
            return Result.info(ResultCode.RESOURCE_NOT_EXIST);
        }
        int followStatus = 0;
        User loginUser = hostHolder.getUser();
        if (loginUser != null) {
            // 登录的情况下才查询是否登录用户是否关注了该问题，followStatus为1表示关注了，为0表示未关注，默认为0。
            followStatus = followService.isFollower(loginUser.getId(),questionId,EntityType.ENTITY_QUESTION) ? 1 : 0;
        }
        // 获取最新关注该问题的10个用户
        List<User> users = questionService.getFollowersUser(questionId);
        return Result.success().put("question_info",question_info)
                .put("follow_status",followStatus)
                .put("follower_count",followService.getFollowerCount(questionId,EntityType.ENTITY_QUESTION))
                .put("follower_users",users)
                .put("comments_info",commentService.selectCommentsByEntity(questionId, EntityType.ENTITY_QUESTION));
    }
}