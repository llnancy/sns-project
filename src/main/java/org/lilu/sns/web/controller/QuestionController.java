package org.lilu.sns.web.controller;

import org.hibernate.validator.constraints.Length;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.pojo.*;
import org.lilu.sns.service.CommentService;
import org.lilu.sns.service.LikeService;
import org.lilu.sns.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
     * 根据questionId获取问题详细信息（包含问题的评论详细信息）
     * @param questionId
     * @return
     */
    @RequestMapping("/{questionId}")
    public Result getQuestionById(@PathVariable("questionId") Integer questionId) {
        ViewObject question_info = questionService.selectQuestionById(questionId);
        if (question_info.get("question") == null) {
            return Result.info(ResultCode.RESOURCE_NOT_EXIST);
        }
        return Result.success().put("question_info",question_info)
                .put("comments_info",commentService.selectCommentsByEntity(questionId, EntityType.ENTITY_QUESTION));
    }
}