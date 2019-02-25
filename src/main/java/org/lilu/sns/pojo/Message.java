package org.lilu.sns.pojo;

import org.lilu.sns.util.CodingUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: lilu
 * @Date: 2019/1/30
 * @Description: 消息中心
 */
public class Message implements Serializable {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String content;
    private Date createdDate;
    private Integer hasRead;
    private String conversationId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
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

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public String getConversationId() {
        // 便于对conversationId这一列进行分组，同一会话消息的conversationId存储一样的数据。
        if (fromId < toId) {
            return CodingUtil.base64Encode(String.format("%d_%d",fromId,toId));
        } else {
            return CodingUtil.base64Encode(String.format("%d_%d",toId,fromId));
        }
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", hasRead=" + hasRead +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }
}