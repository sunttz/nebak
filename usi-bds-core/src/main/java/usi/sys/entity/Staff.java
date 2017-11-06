package usi.sys.entity;

import java.util.Date;

import usi.sys.util.JacksonUtil;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Staff {
	private Long staffId; //员工ID
	private String userId; //登陆账号
	private String operatorName; //员工姓名
	private Long orgId; //所属机构
	private String password; //密码
	private Integer duration; //密码有效期限
	private String picture;//员工照片
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date pwdlastModTime; //密码最后修改时间
	private String email; //邮箱
	private String homeTel; //家庭电话
	private String homeAddress;  //家庭住址
	private Integer zipcode; //家庭邮编
	private String idCardNum; //  身份证号
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date birthday; //生日
	private Integer gender; //性别
	private Integer party; //政治面貌
	private Integer degree; //学历
	private String mobileNum; //手机号码
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date enterTime; //入职时间
	private Integer empType;//用工类型
	private String picUrl; //照片url
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime; //创建时间
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date lastModTime; //员工信息最后修改时间
	private Integer isOnduty; //是否在职
	private Integer status; //是否可用,删除标志
	private String otel; //办公电话
	private Integer ozipCode; //办公邮编
	private String oaddres;	//办公地址
	private String indexCode;	//姓名检索码
	private Integer positionSalary;	//岗位工资（元）
	private Integer performanceSalary;	//绩效基数（元）
	private Integer isLock;	//账号是否被锁定
	private Integer pwdErrTimes; //密码输错次数
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date lstErrPwdTime;	//最后输错密码时间
	
	public String toString(){
		String result = "";
		try {
			result = JacksonUtil.obj2json(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Date getPwdlastModTime() {
		return pwdlastModTime;
	}

	public void setPwdlastModTime(Date pwdlastModTime) {
		this.pwdlastModTime = pwdlastModTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomeTel() {
		return homeTel;
	}

	public void setHomeTel(String homeTel) {
		this.homeTel = homeTel;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getParty() {
		return party;
	}

	public void setParty(Integer party) {
		this.party = party;
	}

	public Integer getDegree() {
		return degree;
	}

	public void setDegree(Integer degree) {
		this.degree = degree;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public Date getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(Date enterTime) {
		this.enterTime = enterTime;
	}

	public Integer getEmpType() {
		return empType;
	}

	public void setEmpType(Integer empType) {
		this.empType = empType;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(Date lastModTime) {
		this.lastModTime = lastModTime;
	}

	public Integer getIsOnduty() {
		return isOnduty;
	}

	public void setIsOnduty(Integer isOnduty) {
		this.isOnduty = isOnduty;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOtel() {
		return otel;
	}

	public void setOtel(String otel) {
		this.otel = otel;
	}

	public String getOaddres() {
		return oaddres;
	}

	public void setOaddres(String oaddres) {
		this.oaddres = oaddres;
	}

	public String getIndexCode() {
		return indexCode;
	}

	public void setIndexCode(String indexCode) {
		this.indexCode = indexCode;
	}

	public Integer getPositionSalary() {
		return positionSalary;
	}

	public void setPositionSalary(Integer positionSalary) {
		this.positionSalary = positionSalary;
	}

	public Integer getPerformanceSalary() {
		return performanceSalary;
	}

	public void setPerformanceSalary(Integer performanceSalary) {
		this.performanceSalary = performanceSalary;
	}

	public Integer getIsLock() {
		return isLock;
	}

	public void setIsLock(Integer isLock) {
		this.isLock = isLock;
	}

	public Date getLstErrPwdTime() {
		return lstErrPwdTime;
	}

	public void setLstErrPwdTime(Date lstErrPwdTime) {
		this.lstErrPwdTime = lstErrPwdTime;
	}

	public Integer getPwdErrTimes() {
		return pwdErrTimes;
	}

	public void setPwdErrTimes(Integer pwdErrTimes) {
		this.pwdErrTimes = pwdErrTimes;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public Integer getOzipCode() {
		return ozipCode;
	}

	public void setOzipCode(Integer ozipCode) {
		this.ozipCode = ozipCode;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	
}
