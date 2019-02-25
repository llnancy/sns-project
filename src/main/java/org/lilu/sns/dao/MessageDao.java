package org.lilu.sns.dao;

import org.apache.ibatis.annotations.*;
import org.lilu.sns.pojo.Message;

import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/30
 * @Description: 消息中心Dao层Mapper接口
 */
@Mapper
public interface MessageDao {
    /**
     * 表名
     */
    String TABLE_NAME = " message ";

    /**
     * 待插入字段名
     */
    String INSERT_FIELDS = " from_id,to_id,content,created_date,has_read,conversation_id ";

    /**
     * 待查询字段名
     */
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 新增一条message记录
     * @param message
     * @return
     */
    @Insert({"insert into " + TABLE_NAME + "(" + INSERT_FIELDS +
            ") values(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int insert(Message message);

    /**
     * 查询一个会话的全部消息
     * @param conversationId
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where conversation_id=#{conversationId} order by created_date desc"})
    List<Message> selectConversationDetail(@Param("conversationId") String conversationId);

    /**
     * 查询一个user的所有会话信息，由于页面上需要显示一个会话共有多少条消息，
     * 而在POJO实体类中没有字段与查询出来的count(id)进行映射，且POJO类的id字段无实际用途，
     * 故将count(id)与POJO类的id字段进行相互映射
     *
     * 注意mysql5.7之后的高版本中的group by子句的校验模式，可修改配置文件屏蔽高版本的规则，否则SQL语句执行将会报错。
     * 或者不修改配置文件而使用any_value()函数，但sql语句可能很奇怪。
     *
     * 子查询中的order by语句如果不加limit限制则会被mysql自动优化，从而order by排序失败
     * limit语句：
     * ①：limit 0,5：从偏移量为0的记录开始取5条记录
     * ②：limit 5 offset 0：兼容PostgreSQL的写法：取5条记录从偏移量为0的记录开始取。
     * 使用limit获取所有记录，低版本可尝试 limit 0,-1
     * 高版本中参数-1已经不再适用，可使用一个很大的数代替，例如2的64次方-1：18446744073709551615（64位操作系统所能处理的最大值）
     *
     * 完整的sql语句：
     * select
     *     count(id) as id,from_id,to_id,content,created_date,has_read,conversation_id
     * from (
     *     select * from message
     *     where from_id=#{localUserId} or to_id=#{localUserId}
     *     // 这个order by是为了之后分组时获得的第一条记录是最新记录
     *     order by created_date desc limit 0, 18446744073709551615
     * ) as m
     * group by conversation_id
     * order by created_date desc
     *
     * @param localUserId
     * @return
     */
    @Select({"select count(id) as id, ",INSERT_FIELDS," from ",
            " (select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where (from_id=#{localUserId} or to_id=#{localUserId}) and order by created_date desc limit 0, 18446744073709551615) as m ",
            " group by conversation_id order by created_date desc "})
    List<Message> selectConversationList(@Param("localUserId") int localUserId);

    /**
     * 查询指定用户的指定会话的未读消息数量（未读消息：has_read字段的值为1）
     * @param localUserId
     * @param conversationId
     * @return
     */
    @Select({"select count(id) from ",TABLE_NAME,
            " where has_read=1 and to_id=#{localUserId} and conversation_id=#{conversationId}"})
    int selectConversationUnreadCount(@Param("localUserId") int localUserId,
                                      @Param("conversationId") String conversationId);


    /**
     * 将指定用户在指定会话中收到的消息设为已读（已读：has_read字段的值为0）
     * @param toUserId
     * @param conversationId
     */
    @Update({"update ",TABLE_NAME," set has_read=0 where to_id=#{toId} and conversation_id=#{conversationId}"})
    void updateMessageReadStatus(@Param("toId") int toUserId,@Param("conversationId") String conversationId);
}