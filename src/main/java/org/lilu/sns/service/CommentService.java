package org.lilu.sns.service;

import org.lilu.sns.dao.CommentDao;
import org.lilu.sns.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/29
 * @Description: 评论中心业务层
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    /**
     * 添加一条评论
     * @param comment 过滤评论内容的HTML标签和敏感词
     * @return
     */
    public int insertComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.insert(comment);
    }

    /**
     * 根据实体类型查询所有评论（包括与评论相关联的user信息）
     * @param entityId
     * @param entityType
     * @return
     */
    public List<ViewObject> selectCommentsByEntity(int entityId,int entityType) {
        List<Comment> comments = commentDao.selectCommentsByEntity(entityId,entityType);
        List<ViewObject> vos = new ArrayList<>();
        User hostHolderUser = hostHolder.getUser();
        for (Comment comment : comments) {
            ViewObject vo = getCommentDetail(comment);
            // 加入当前登录用户对评论的点赞点踩状态
            vo.set("like_status",hostHolderUser == null ? 0 : likeService.getLikeStatus(hostHolderUser.getId(),comment.getId(),EntityType.ENTITY_COMMENT));
            // 加入评论的点赞数
            vo.set("like_count",likeService.getLikeCount(comment.getId(),EntityType.ENTITY_COMMENT));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 根据commentId查询评论
     * @param commentId
     * @return
     */
    public Comment selectCommentById(int commentId) {
        return commentDao.selectCommentById(commentId);
    }

    /**
     * 查询评论的数量
     * @param entityId
     * @param entityType
     * @return
     */
    public int selectCommentCount(int entityId,int entityType) {
        return commentDao.selectCommentCount(entityId,entityType);
    }

    /**
     * 提取公共方法：传入一个comment，返回一个包含comment相关信息和user的ViewObject
     * @param comment
     * @return
     */
    private ViewObject getCommentDetail(Comment comment) {
        ViewObject vo = new ViewObject();
        if (comment != null) {
            User user = userService.selectUserById(comment.getUserId());
            // 还原html的转义给前端
//            comment.setContent(HtmlUtils.htmlUnescape(comment.getContent()));
            vo.set("comment",comment);
            vo.set("user",userService.removeSensitiveFields(user));
        }
        return vo;
    }
}