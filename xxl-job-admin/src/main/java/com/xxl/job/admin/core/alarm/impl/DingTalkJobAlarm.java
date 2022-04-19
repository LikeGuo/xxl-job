package com.xxl.job.admin.core.alarm.impl;

import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.xxl.job.admin.core.alarm.ProjectEnum;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

@Component
public class DingTalkJobAlarm extends AbstractJobAlarm {
	
	private static final Logger log = LoggerFactory.getLogger(DingTalkJobAlarm.class);
	
	@Value("${job.msearch.dingtalk.robot.webhook}")
	private String msearchWebhook;
	@Value("${job.msearch.dingtalk.robot.secret}")
	private String msearchSecret;
	@Value("${job.yxf.dingtalk.robot.webhook}")
	private String yxfWebhook;
	@Value("${job.yxf.dingtalk.robot.secret}")
	private String yxfSecret;
	@Value("${job.mjd.dingtalk.robot.webhook}")
	private String mjdWebhook;
	@Value("${job.mjd.dingtalk.robot.secret}")
	private String mjdSecret;
	@Value("${job.kefang.dingtalk.robot.webhook}")
	private String kefangWebhook;
	@Value("${job.kefang.dingtalk.robot.secret}")
	private String kefangSecret;
	@Value("${job.erp.dingtalk.robot.webhook}")
	private String erpWebhook;
	@Value("${job.erp.dingtalk.robot.secret}")
	private String erpSecret;

	@Override
	public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
		boolean alarmResult = true;
		// send monitor dingtalk
        if (info!=null && !ProjectEnum.NULL.name().equals(info.getAlarmDingTalk())) {
        	try {
        		XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
        		
        		Long timestamp = System.currentTimeMillis();
        		
        		String stringToSign = timestamp + "\n" + getSecret(info.getAlarmDingTalk());
        		Mac mac = Mac.getInstance("HmacSHA256");
        		mac.init(new SecretKeySpec(getSecret(info.getAlarmDingTalk()).getBytes("UTF-8"), "HmacSHA256"));
        		byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        		String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        		
        		DingTalkClient client = new DefaultDingTalkClient(getWebhook(info.getAlarmDingTalk()) + "&timestamp=" + timestamp + "&sign=" + sign);
        		OapiRobotSendRequest request = new OapiRobotSendRequest();
        		
        		OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        		//推送指定用户
//        		String[] mobiles = info.getAlarmDingTalk().split(",");
//        		at.setAtMobiles(Arrays.asList(mobiles));
        		at.setIsAtAll(true);
        		request.setAt(at);
        		//文本消息
        		request.setMsgtype("text");
        		OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        		text.setContent(getAlarmContent(info, jobLog, group));
        		request.setText(text);
        		
        		OapiRobotSendResponse response = client.execute(request);
			} catch (Exception e) {
				log.error("Failed to send dingding alarm message", e);
				alarmResult = false;
			}
        }
		return alarmResult;
	}
	
	private String getSecret(String code) {
		if (ProjectEnum.MSearch.name().equals(code)) {
			return msearchSecret;
		} else if (ProjectEnum.YXF.name().equals(code)){
			return yxfSecret;
		} else if (ProjectEnum.MJD.name().equals(code)){
			return mjdSecret;
		} else if (ProjectEnum.KeFang.name().equals(code)){
			return kefangSecret;
		} else if (ProjectEnum.ERP.name().equals(code)){
			return erpSecret;
		}
		return null;
	}
	
	private String getWebhook(String code) {
		if (ProjectEnum.MSearch.name().equals(code)) {
			return msearchWebhook;
		} else if (ProjectEnum.YXF.name().equals(code)){
			return yxfWebhook;
		} else if (ProjectEnum.MJD.name().equals(code)){
			return mjdWebhook;
		} else if (ProjectEnum.KeFang.name().equals(code)){
			return kefangWebhook;
		} else if (ProjectEnum.ERP.name().equals(code)){
			return erpWebhook;
		}
		return null;
	}
}
