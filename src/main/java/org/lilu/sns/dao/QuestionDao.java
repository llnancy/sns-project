package org.lilu.sns.dao;

import org.apache.ibatis.annotations.*;
import org.lilu.sns.pojo.Question;

import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description: 问题Dao层mapper接口
 */
@Mapper
public interface QuestionDao {
    /**
     * 表名
     */
    String TABLE_NAME = " question ";

    /**
     * 待插入字段名
     */
    String INSERT_FIELDS = " title,content,created_date,user_id,comment_count ";

    /**
     * 待查询字段名
     */
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into " + TABLE_NAME + "(" + INSERT_FIELDS +
            ") values(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int insert(Question question);

    /**
     * 查询最新问题
     * @return
     */
    List<Question> selectLatestQuestions(@Param("userId") int userId);

    /**
     * 根据id查询question
     * @param questionId
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{questionId}"})
    Question selectById(int questionId);

    /**
     * 更新问题的评论数量
     * @param entityId
     * @param count
     * @return
     */
    @Update({"update ",TABLE_NAME," set comment_count=#{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int entityId,@Param("commentCount") int count);
}