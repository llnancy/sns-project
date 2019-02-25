package org.lilu.sns.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.lilu.sns.pojo.User;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description: 用户Dao层mapper接口
 */
@Mapper
public interface UserDao {
    /**
     * 表名
     */
    String TABLE_NAME = " user ";

    /**
     * 待插入字段名
     */
    String INSERT_FIELDS = " login_name,nick_name,password,salt,avatar,gender,email,phone_number ";

    /**
     * 待查询字段名
     */
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 插入user记录，将插入记录的主键id反填到user对象中
     * @param user
     * @return
     */
    @Insert({"insert into " + TABLE_NAME + " ( " + INSERT_FIELDS +
            " ) values(#{loginName},#{nickName},#{password},#{salt},#{avatar},#{gender},#{email},#{phoneNumber})"})
    @Options(useGeneratedKeys = true)
    int insert(User user);

    /**
     * 根据id查询user
     * @param id
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    User selectById(int id);

    /**
     * 根据login_name查询user
     * @param loginName
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where login_name=#{loginName}"})
    User selectByLoginName(String loginName);

    /**
     * 根据邮箱email查询user
     * @param email
     * @return
     */
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where email=#{email}"})
    User selectByEmail(String email);
}