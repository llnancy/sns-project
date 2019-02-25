package org.lilu.sns.dao;

import org.apache.ibatis.annotations.*;
import org.lilu.sns.pojo.LoginTicket;

import java.util.List;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description: 用户登录信息Dao层mapper接口
 */
@Mapper
public interface LoginTicketDao {
    /**
     * 表名
     */
    String TABLE_NAME = " login_ticket ";

    /**
     * 待插入字段名
     */
    String INSERT_FIELDS = " user_id,ticket,expired,status ";

    /**
     * 待查询字段名
     */
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 添加一条loginTicket记录
     * @param loginTicket
     * @return
     */
    @Insert({"insert into ",TABLE_NAME," ( ",INSERT_FIELDS," ) values(#{userId},#{ticket},#{expired},#{status})"})
    int insert(LoginTicket loginTicket);

    /**
     * 根据ticket查询loginTicket记录
     * @param ticket
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    /**
     * 根据userId查询loginTicket记录
     * @param userId
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where user_id=#{userId}"})
    List<LoginTicket> selectByUserId(int userId);

    /**
     * 更新对应loginTicket的status状态
     * @param ticket
     * @param status
     */
    @Update({"update ",TABLE_NAME," set status=#{status} where ticket=#{ticket}"})
    int updateStatus(@Param("ticket") String ticket, @Param("status") Integer status);
}