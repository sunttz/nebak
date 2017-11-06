package usi.sys.job.entity;

public class SysJob {
	private Long jobId;			//主键
	private String jobName;		//任务名称
	private String jobGroup;	//任务分组
	private String jobBeanName;	//任务类型
	private String cronExpr;	//CRON表达式
	private String inParam;		//任务参数
	private int currState;		//当前状态 当前状态（0未启动1已启动）
	private int isAutoStart;	//是否随工程启动 （0不随工程启动1随工程启动）
	private String ipAddr;		//服务器IP地址
	private int isDel;			//是否删除 （0未删除1已删除）
	private String jobMemo;		//备注
	private long staffId;		//创建人
	private String staffName;	//创建人
	private String createTime;	//创建时间
	
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	private String lastModTime;	//最后修改时间
	
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobBeanName() {
		return jobBeanName;
	}
	public void setJobBeanName(String jobBeanName) {
		this.jobBeanName = jobBeanName;
	}
	public String getCronExpr() {
		return cronExpr;
	}
	public void setCronExpr(String cronExpr) {
		this.cronExpr = cronExpr;
	}
	public int getCurrState() {
		return currState;
	}
	public void setCurrState(int currState) {
		this.currState = currState;
	}
	public int getIsAutoStart() {
		return isAutoStart;
	}
	public void setIsAutoStart(int isAutoStart) {
		this.isAutoStart = isAutoStart;
	}
	public int getIsDel() {
		return isDel;
	}
	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	public String getJobMemo() {
		return jobMemo;
	}
	public void setJobMemo(String jobMemo) {
		this.jobMemo = jobMemo;
	}
	public long getStaffId() {
		return staffId;
	}
	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLastModTime() {
		return lastModTime;
	}
	public void setLastModTime(String lastModTime) {
		this.lastModTime = lastModTime;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getInParam() {
		return inParam;
	}
	public void setInParam(String inParam) {
		this.inParam = inParam;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	
}
