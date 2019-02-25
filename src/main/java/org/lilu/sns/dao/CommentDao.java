package org.lilu.sns.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lilu.sns.pojo.Comment;

import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/29
 * @Description: 评论中心Dao层mapper接口
 */
@Mapper
public interface CommentDao {
    /**
     * 表名
     */
    String TABLE_NAME = " comment ";

    /**
     * 待插入字段名
     */
    String INSERT_FIELDS = " user_id,entity_id,entity_type,content,created_date,status ";

    /**
     * 待查询字段名
     */
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 新增一条comment记录
     * @param comment
     * @return
     */
    @Insert({"insert into " + TABLE_NAME + "(" + INSERT_FIELDS +
            ") values(#{userId},#{entityId},#{entityType},#{content},#{createdDate},#{status})"})
    int insert(Comment comment);

    /**
     * 根据实体类型查询所有评论
     * @param entityId
     * @param entityType
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentsByEntity(@Param("entityId") int entityId,@Param("entityType") int entityType);

    /**
     * 根据id查询评论
     * @param commentId
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{commentId}"})
    Comment selectCommentById(int commentId);

    /**
     * 根据实体类型查询对应的评论数量
     * @param entityId
     * @param entityType
     * @return
     */
    @Select({"select count(id) from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    int selectCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);
}