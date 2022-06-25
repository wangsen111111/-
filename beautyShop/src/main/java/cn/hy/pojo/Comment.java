package cn.hy.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品评价
 */
public class Comment implements Serializable{

    private Integer id;

    public Integer userId;

    private User user;

    public Integer itemId;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    private Date addTime;

    /**
     * 对商品评分
     */
    public Integer score;

    /**
     * 用户对商品的评分
     */
    public Comment(Integer itemId, Integer score) {
        this.itemId=itemId;
        this.score=score;
    }
//    @Override
//    public int compareTo(Comment o) {
//        return score > o.score ? -1 : 1;
//    }

    public Comment() {

    }

    public Comment(Integer userId) {
        this.userId=userId;
    }



    /**
     *用户对应的商品集合
     */
    public List<Comment> itemList = new ArrayList<>();
    public Comment set(Integer itemId, int score) {
        this.itemList.add(new Comment(itemId, score));
        return this;
    }

    public Comment find(Integer itemId) {
        for (Comment item : itemList) {
            if (item.itemId.equals(itemId)) {
                return item;
            }
        }
        return null;
    }


    public Comment(Integer id, Integer userId, Integer itemId, String content, Date addTime,Integer score) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.content = content;
        this.addTime = addTime;
        this.score=score;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", user=" + user +
                ", itemId=" + itemId +
                ", content='" + content + '\'' +
                ", addTime=" + addTime +
                ", score=" + score +
                '}';
    }
}
