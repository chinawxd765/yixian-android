package com.chuxin.yixian.enumType;


/**
 * 学历枚举
 * 
 * <pre>
 * Filename      :  EducationEnum.java
 * PackageName   :  com.chuxin.yixian.base.module.user.enumType
 * copyright     :  Copyright (c)2016
 * Company       :  上海初心电子商务有限公司
 * Create Date   :  2016年11月28日
 * </pre>
 * 
 * @version : 1.0
 * @author : wujunda
 */
public enum EducationEnum {

	/**
	 * 高中以下
	 */
	BELOW_HIGH_SCHOOL("高中以下"),
	
	/**
	 * 高中
	 */
	HIGH_SCHOOL("高中"),
	
	/**
	 * 大专
	 */
	COLLEGE_DEGREE("大专"),
	
	/**
	 * 本科
	 */
	UNIVERSITY_DEGREE("本科"),
	
	/**
	 * 硕士研究生
	 */
	MASTER("硕士研究生"),
	
	/**
	 * 博士研究生
	 */
	DOCTOR("博士研究生");
    
	private String description;
    
    private EducationEnum(String description) {
    	this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
