package com.chuxin.yixian.model;


/**
 * Created by ASUS on 2016/11/30.
 */
public class UserImage {

    private long id;  // 用户图片主键ID
    private String imageSrc;  // 用户图片

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}
