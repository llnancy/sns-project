package org.lilu.sns.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.lilu.sns.pojo.Feed;

/**
 * @Auther: lilu
 * @Date: 2019/4/6
 * @Description:
 */
@Mapper
public interface FeedDao {
    /**
     * 表名
     */
    String TABLE_NAME = " feed ";

    /**
     * 待插入字段名
     */
    String INSERT_FIELDS = " type,user_id,created_date,data ";

    /**
     * 待查询字段名
     */
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({" insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{type},#{userId},#{createdDate},#{data})"})
    int insertFeed(Feed feed);
}
