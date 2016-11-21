package com.chuxin.yixian.model;


/**
 * Created by ASUS on 2016/11/15.
 */
public class User {

    private int id;  // 主键ID
    private String nickName;  // 昵称
    private String sex;  // 性别
    private String headImageSrc;    // 头像
    private String mood;  // 心情

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeadImageSrc() {
        return headImageSrc;
    }

    public void setHeadImageSrc(String headImageSrc) {
        this.headImageSrc = headImageSrc;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
