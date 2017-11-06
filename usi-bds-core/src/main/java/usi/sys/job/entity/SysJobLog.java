package usi.sys.job.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 定时任务执行日志
 * @author lmwang
 * 创建时间：2014-7-4 上午10:23:30
 */
public class SysJobLog {

	//定时任务日志id
	private long jobLogId;
	
	//任务主键id
	private long jobId;
	
	//任务名称
	private String jobName;	
	
	//绑定执行IP地址
	private  String ipAddr;
	
	//任务执行开始时间（用于从数据库查询出来日期响应到页面json格式的转换，注意设置timezone，否则默认timezone可能有问题）
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date jobStartTime;
	
	//任务执行结束时间（用于从数据库查询出来日期响应到页面json格式的转换，注意设置timezone，否则默认timezone可能有问题）
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date jobEndTime;
	
	//是否正常结束（0异常结束1正常结束）
	private int isSuccessEnd;
	
	public long getJobLogId() {
		return jobLogId;
	}

	public void setJobLogId(long jobLogId) {
		this.jobLogId = jobLogId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(Date jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public Date getJobEndTime() {
		return jobEndTime;
	}

	public void setJobEndTime(Date jobEndTime) {
		this.jobEndTime = jobEndTime;
	}

	public int getIsSuccessEnd() {
		return isSuccessEnd;
	}

	public void setIsSuccessEnd(int isSuccessEnd) {
		this.isSuccessEnd = isSuccessEnd;
	}
	
}
