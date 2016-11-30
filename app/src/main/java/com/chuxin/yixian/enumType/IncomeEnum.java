package com.chuxin.yixian.enumType;


/**
 * 年收入枚举
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
public enum IncomeEnum {

	/**
	 * 5万以下
	 */
	BELOW_50_THOUSAND("5万以下"),
	
	/**
	 * 5万-10万
	 */
	BETWEEN_50_TO_100_THOUSAND("5万-10万"),
	
	/**
	 * 10万-20万
	 */
	BETWEEN_100_TO_200_THOUSAND("10万-20万"),
	
	/**
	 * 20万-30万
	 */
	BETWEEN_200_TO_300_THOUSAND("20万-30万"),
	
	/**
	 * 30万-50万
	 */
	BETWEEN_300_TO_500_THOUSAND("30万-50万"),
	
	
	/**
	 * 50万以上
	 */
	MORETHAN_500_THOUSAND("50万以上");
    
	private String description;
    
    private IncomeEnum(String description) {
    	this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
