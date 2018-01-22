package usi.biz.entity;

public class AutoLogDto {
	//ID
	private Long serverId;
	//机构ID
	private Long orgId;
	//机构名称
	private String orgName;
	//设备名称
	private String deviceName;
	//设备类型
	private String deviceType;
	//备注
	private String remarks;
	//设备地址
	private String deviceAddr;
	//备份路径
	private String bakPath;
	//备份类型
	private String bakType;
	//用户名
	private String userName;
	//密码
	private String passWord;
	//ID
	private Long  logId;
	//备份结果
	private int bakFlag;
	//创建时间
	private String createDate;
	// 保存类型
	private String saveType;
	// 保存份数
	private Long saveDay;
	
	public Long getServerId() {
		return serverId;
	}
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDeviceAddr() {
		return deviceAddr;
	}
	public void setDeviceAddr(String deviceAddr) {
		this.deviceAddr = deviceAddr;
	}
	public String getBakPath() {
		return bakPath;
	}
	public void setBakPath(String bakPath) {
		this.bakPath = bakPath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public Long getLogId() {
		return logId;
	}
	public void setLogId(Long logId) {
		this.logId = logId;
	}
	public int getBakFlag() {
		return bakFlag;
	}
	public void setBakFlag(int bakFlag) {
		this.bakFlag = bakFlag;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getBakType() {
		return bakType;
	}

	public void setBakType(String bakType) {
		this.bakType = bakType;
	}

	public Long getSaveDay() {
		return saveDay;
	}

	public void setSaveDay(Long saveDay) {
		this.saveDay = saveDay;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
}
