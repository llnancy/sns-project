package org.lilu.sns.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Auther: lilu
 * @Date: 2019/2/9
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;

    @Test
    public void selectLatestQuestions() {
    }

    @Test
    public void insertQuestion() {
    }

    @Test
    public void selectQuestionById() {
    }

    @Test
    public void updateCommentCount() {
        int count = questionService.updateCommentCount(1,13);
        System.out.println(count);
    }
}