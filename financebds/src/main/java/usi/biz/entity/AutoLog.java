package usi.biz.entity;

import java.util.Date;

/**
 * 自动网元备份结果日志
 * @author 
 *
 */
public class AutoLog {
	//ID
	private Long  logId;
	//网元ID
	private Long serverId;
	//备份结果
	private int bakFlag;
	//创建时间
	private Date createDate;
	public Long getLogId() {
		return logId;
	}
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	public Long getServerId() {
		return serverId;
	}
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	public int getBakFlag() {
		return bakFlag;
	}
	public void setBakFlag(int bakFlag) {
		this.bakFlag = bakFlag;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
