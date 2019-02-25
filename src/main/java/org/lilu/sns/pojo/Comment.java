package org.lilu.sns.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: lilu
 * @Date: 2019/1/29
 * @Description: 评论中心
 */
public class Comment implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer entityId;
    private Integer entityType;
    private String content;
    private Date createdDate;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityId=" + entityId +
                ", entityType=" + entityType +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", status=" + status +
                '}';
    }
}