package org.lilu.sns.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lilu.sns.pojo.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class QuestionDaoTest {
    @Autowired
    private QuestionDao questionDao;

    @Test
    public void initDatabase() {
        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
            Question question = new Question();
            question.setTitle("关于轮播图展示的问题" + i);
            question.setContent("前不久呢，景安出了一个云服务器试用的活动，配置有两种，一种是4核CPU，4G内存，5M带宽的云服务器，1元30天，但是限制是企业用户，谁要是能搞到营业执照就去弄一个。 还有一种，1核CPU，1G内存，1M带宽的VPS，也是1元30天，是个人用户的，测试一些小程序是足够用了，大家有需求的可以去弄几个备用。");
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setUserId(i);
            question.setCommentCount(i);
            questionDao.insert(question);
        }
    }

    @Test
    public void selectLatestQuestionsTest() {
        List<Question> questions = questionDao.selectLatestQuestions(0);
        System.out.println(questions.size());
        System.out.println("ok");
        for (Question question : questions) {
            System.out.println(question);
        }
    }

    @Test
    public void selectById() {
        Question question = questionDao.selectById(1);
        System.out.println(question);
    }
}