package org.lilu.sns.service;

import org.lilu.sns.dao.QuestionDao;
import org.lilu.sns.pojo.Question;
import org.lilu.sns.pojo.User;
import org.lilu.sns.pojo.ViewObject;
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

    /**
     * 查询最新问题
     * @param userId
     * @return
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
     * @param question
     * @return
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
     * @param questionId
     * @return
     */
    public ViewObject selectQuestionById(int questionId) {
        return getQuestionDetail(questionDao.selectById(questionId));
    }

    /**
     * 提取公共方法：传入一个question，返回一个包含question和user的ViewObject
     * @param question
     * @return
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
     * @param entityId
     * @param count
     * @return
     */
    public int updateCommentCount(int entityId, int count) {
        return questionDao.updateCommentCount(entityId,count);
    }
}