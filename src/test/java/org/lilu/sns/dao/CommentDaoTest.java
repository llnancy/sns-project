package org.lilu.sns.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lilu.sns.pojo.Comment;
import org.lilu.sns.pojo.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Auther: lilu
 * @Date: 2019/1/30
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentDaoTest {

    @Autowired
    private CommentDao commentDao;

    @Test
    public void insert() {
        Comment comment = new Comment();
        comment.setUserId(1);
        comment.setContent("balabalababalala");
        comment.setCreatedDate(new Date());
        comment.setEntityId(1);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setStatus(0);
        commentDao.insert(comment);
    }

    @Test
    public void selectCommentsByEntity() {
        List<Comment> comments = commentDao.selectCommentsByEntity(1,EntityType.ENTITY_QUESTION);
        for (Comment comment : comments) {
            System.out.println(comment);
        }
    }
}