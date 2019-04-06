package org.lilu.sns.service;

import org.lilu.sns.dao.QuestionDao;
import org.lilu.sns.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description: 问题中心业务层
 */
@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService  followService;

    /**
     * 查询最新问题
     * @param userId 用户id
     * @return vo的集合
     */
    public List<ViewObject> selectLatestQuestions(int userId) {
        List<Question> questions = questionDao.selectLatestQuestions(userId);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questions) {
            ViewObject vo = getQuestionDetail(question);
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 添加一个问题
     * @param question 问题
     * @return 添加的问题数量
     */
    public int insertQuestion(Question question) {
        // 过滤HTML标签
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        // 敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDao.insert(question);
    }

    /**
     * 根据id获取question详细（包含user）信息
     * @param questionId 问题id
     * @return vo
     */
    public ViewObject selectQuestionById(int questionId) {
        return getQuestionDetail(questionDao.selectById(questionId));
    }

    /**
     * 根据id获取question信息（不包含其它user信息）
     * @param questionId 问题id
     * @return 问题
     */
    public Question selectQuestionByQuestionId(int questionId) {
        return questionDao.selectById(questionId);
    }

    /**
     * 提取公共方法：传入一个question，返回一个包含question和user的ViewObject
     * @param question 问题
     * @return vo
     */
    public ViewObject getQuestionDetail(Question question) {
        ViewObject vo = new ViewObject();
        if (question != null) {
            User user = userService.selectUserById(question.getUserId());
            vo.set("question",question);
            vo.set("user",userService.removeSensitiveFields(user));
        }
        return vo;
    }

    /**
     * 更新问题的评论数量
     * @param entityId 实体id
     * @param count 评论数量
     * @return 数据库中影响的记录行数
     */
    public int updateCommentCount(int entityId, int count) {
        return questionDao.updateCommentCount(entityId,count);
    }

    /**
     * 获取最近关注该问题的10个用户
     * @param questionId 问题id
     * @return 用户集合
     */
    public List<User> getFollowersUser(int questionId) {
        List<User> users = new ArrayList<>();
        // 获取关注该问题的最新的前10个用户的id组
        List<Integer> userIds = followService.getFollowers(questionId, EntityType.ENTITY_QUESTION,10);
        // 遍历获取用户加入集合中
        for (Integer userId : userIds) {
            users.add(userService.removeSensitiveFields(userService.selectUserById(userId)));
        }
        return users;
    }
}