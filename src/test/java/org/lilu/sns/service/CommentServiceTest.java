package org.lilu.sns.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lilu.sns.pojo.Comment;
import org.lilu.sns.pojo.EntityType;
import org.lilu.sns.pojo.ViewObject;
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
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    public void insertComment() {
        Comment comment = new Comment();
        comment.setUserId(1);
        comment.setContent("评论评论评论评论评论评论");
        comment.setCreatedDate(new Date());
        comment.setEntityId(1);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setStatus(0);
        commentService.insertComment(comment);
    }

    @Test
    public void getCommentsByEntity() {
        List<ViewObject> vos = commentService.selectCommentsByEntity(1,EntityType.ENTITY_QUESTION);
        for (ViewObject vo : vos) {
            System.out.println(vo);
        }
    }

    @Test
    public void getCommentCount() {
        int count = commentService.selectCommentCount(1,EntityType.ENTITY_QUESTION);
        System.out.println(count);
    }
}