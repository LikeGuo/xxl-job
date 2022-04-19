package com.xxl.job.admin.core.alarm.impl;

import java.text.MessageFormat;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

public abstract class AbstractJobAlarm implements JobAlarm  {
	
	public String getAlarmContent(XxlJobInfo info, XxlJobLog jobLog, XxlJobGroup group) {
		return MessageFormat.format(loadJobAlarmTemplate(), 
				getAlarmType(jobLog.getHandleCode()),
				info.getId(),
				info.getJobDesc(),
				group.getTitle(), 
				clearupMsg(jobLog.getTriggerMsg()));
	}
	
	private String loadJobAlarmTemplate() {	
		StringBuilder sb = new StringBuilder();
		sb.append("报警类型：{0}\n");
		sb.append("任务ID：{1}\n");
		sb.append("任务描述：{2}\n");
		sb.append("执行器：{3}\n");
		sb.append("{4}\n");
		return sb.toString();
	}
	
	private String getAlarmType(int handleCode) {
		return handleCode == 500 ? "执行失败" : "调度失败";
	}
	
	private String clearupMsg(String msg) {
		return msg.replace("<br><span style=\"color:#00c0ef;\" >", "").replace("</span><br>触发调度：", "").replace("</span>", "").replace("<br>", "\n");
	}
}
