package usi.sys.entity;

import java.util.Date;

/**
 * 附件操作（下载删除）表实体
 * @author lmwang
 * 创建时间：2014-4-13 上午8:45:31
 */
public class SysFileOpLog {
	
	//附件操作主键
	private long opLogId;
	
	//附件主键
	private long fileId;
	
	//操作人
	private long staffId;
	
	//操作人姓名
	private String operatorName;
	
	//操作人机构ID
	private long orgId;
	
	//操作人机构名称
	private String orgName;
	
	//操作时间
	private Date opTime;
	
	//操作类型(0删除1下载)
	private int opType;

	public long getOpLogId() {
		return opLogId;
	}

	public void setOpLogId(long opLogId) {
		this.opLogId = opLogId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Date getOpTime() {
		return opTime;
	}

	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}

	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}
	
	
}
