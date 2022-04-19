package com.xxl.job.admin.core.alarm.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

@Component
public class KeFangJobAlarm extends AbstractJobAlarm {
	
	private static final Logger log = LoggerFactory.getLogger(KeFangJobAlarm.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${job.kefang.robot.webhook}")
	private String webhook;

	@Override
	public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
		boolean alarmResult = true;
		if (info!=null && !StringUtils.isEmpty(info.getAlarmKeFang())) {
			try {
        		XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
        		
        		Map<String, Object> body = new HashMap<>();
        		body.put("msgType", "text");
        		body.put("text", getAlarmContent(info, jobLog, group));
        		body.put("mobile", info.getAlarmKeFang().split(","));
        		
        		HttpHeaders httpHeaders = new HttpHeaders();
        		httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        		
        		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, httpHeaders);
        		
        		ResponseEntity<HashMap> response = restTemplate.postForEntity(webhook, httpEntity, HashMap.class);
        		if (response.getStatusCodeValue() == 200) {
        			HashMap<String, Object> imResult = response.getBody();
        			int code = (int) imResult.get("code");
        			if (code != 0) {
        				alarmResult = false;
					}
				} else {
					alarmResult = false;
				}
			} catch (Exception e) {
				log.error("Failed to send kefang alarm message", e);
				alarmResult = false;
			}
		}
		return alarmResult;
	}
}
