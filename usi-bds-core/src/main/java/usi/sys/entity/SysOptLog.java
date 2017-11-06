package usi.sys.entity;

import java.util.Date;

/**
 * @Description 操作日志实体类
 * @author zhang.dechang
 * @date 2014年12月18日 上午10:50:08
 */
public class SysOptLog {

	private Long optLogId;	//操作记录ID
	
	private String optContent;	//操作内容记录

	private String optIp;	//登陆IP

	private String optModule;	//操作模块:登陆、角色管理...

	private String optName;	//操作名称login、update、delete、insert

	private Date optTime;	//操作时间

	private String userId;	//操作人

	public Long getOptLogId() {
		return optLogId;
	}
	public void setOptLogId(Long optLogId) {
		this.optLogId = optLogId;
	}
	public String getOptContent() {
		return optContent;
	}
	public void setOptContent(String optContent) {
		this.optContent = optContent;
	}
	public String getOptIp() {
		return optIp;
	}
	public void setOptIp(String optIp) {
		this.optIp = optIp;
	}
	public String getOptModule() {
		return optModule;
	}
	public void setOptModule(String optModule) {
		this.optModule = optModule;
	}
	public String getOptName() {
		return optName;
	}
	public void setOptName(String optName) {
		this.optName = optName;
	}
	public Date getOptTime() {
		return optTime;
	}
	public void setOptTime(Date optTime) {
		this.optTime = optTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}