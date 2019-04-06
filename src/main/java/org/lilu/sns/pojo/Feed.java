package org.lilu.sns.pojo;

import java.util.Date;

/**
 * @Auther: lilu
 * @Date: 2019/4/6
 * @Description:
 */
public class Feed {
    private int id;
    // 新鲜事的类型
    private int type;
    // 产生新鲜事的用户id
    private int userId;
    private Date createdDate;
    private String data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
