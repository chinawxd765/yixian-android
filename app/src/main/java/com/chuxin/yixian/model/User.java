package com.chuxin.yixian.model;


/**
 * Created by ASUS on 2016/11/15.
 */
public class User {

    private long id;  // 主键ID
    private String nickName;  // 昵称
    private String sex;  // 性别
    private String headImageSrc;    // 头像
    private String sign;  // 签名
//    private String hometown;  // 家乡
//    private String school;  // 学校
//    private String major;  // 专业
//    private String grade;  // 年级
//    private String education;  // 学历
//    private String residence;  // 现居住地
//    private String profession;  //  职业
//    private String income;  //  收入
//    private String personality;  //  性格描述
//    private String hobbies;  //  兴趣爱好
//    private String expectation;  //  事业展望
//    private String description;  //  个人说明

    private String age;  // 年龄

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getSign() {
        return sign;
    }

    public void setSign(String mood) {
        this.sign = mood;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

//    public String getHometown() {
//        return hometown;
//    }
//
//    public void setHometown(String hometown) {
//        this.hometown = hometown;
//    }
//
//    public String getSchool() {
//        return school;
//    }
//
//    public void setSchool(String school) {
//        this.school = school;
//    }
//
//    public String getMajor() {
//        return major;
//    }
//
//    public void setMajor(String major) {
//        this.major = major;
//    }
//
//    public String getGrade() {
//        return grade;
//    }
//
//    public void setGrade(String grade) {
//        this.grade = grade;
//    }
//
//    public String getEducation() {
//        return education;
//    }
//
//    public void setEducation(String education) {
//        this.education = education;
//    }
//
//    public String getResidence() {
//        return residence;
//    }
//
//    public void setResidence(String residence) {
//        this.residence = residence;
//    }
//
//    public String getProfession() {
//        return profession;
//    }
//
//    public void setProfession(String profession) {
//        this.profession = profession;
//    }
//
//    public String getIncome() {
//        return income;
//    }
//
//    public void setIncome(String income) {
//        this.income = income;
//    }
//
//    public String getPersonality() {
//        return personality;
//    }
//
//    public void setPersonality(String personality) {
//        this.personality = personality;
//    }
//
//    public String getHobbies() {
//        return hobbies;
//    }
//
//    public void setHobbies(String hobbies) {
//        this.hobbies = hobbies;
//    }
//
//    public String getExpectation() {
//        return expectation;
//    }
//
//    public void setExpectation(String expectation) {
//        this.expectation = expectation;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

}
