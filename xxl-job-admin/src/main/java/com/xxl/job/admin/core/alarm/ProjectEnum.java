package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.util.I18nUtil;

public enum ProjectEnum {
	NULL(I18nUtil.getString("system_please_choose")),
	YXF(I18nUtil.getString("alarm_dingtalk_group_yxf")),
	MJD(I18nUtil.getString("alarm_dingtalk_group_mjd")),
	KeFang(I18nUtil.getString("alarm_dingtalk_group_kefang")),
	ERP(I18nUtil.getString("alarm_dingtalk_group_erp")),
	MSearch(I18nUtil.getString("alarm_dingtalk_group_msearch"));
	
	ProjectEnum(String title){
		this.title = title;
	}
	
	private String title;
	
	public String getTitle() {
		return title;
	}
}
