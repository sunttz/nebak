package usi.sys.dto;

import java.util.Date;
import java.util.List;

import usi.sys.util.JacksonUtil;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StaffInfo {
	private Long staffId; //员工ID
	private String userId; //登陆账号
	private String operatorName; //员工姓名
	private Long orgId; //所属机构
	private String orgName;//机构名称
	private String orgSeq;	//机构序列
	private long areaOrgId;//本地网机构ID（如果是省公司下直接挂人，就是省公司机构id）
	private long rootOrgId;//根机构ID（省公司机构id）
	private String password; //密码
	private Integer duration; //密码有效期限
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date pwdlastModTime; //密码最后修改时间
	private String email; //邮箱
	private String homeTel; //家庭电话
	private String homeAddress;  //家庭住址
	private String zipcode; //家庭邮编
	private String idCardNum; //  身份证号
	private Integer gender; //性别
	private String mobileNum; //手机号码
	private String otel; //办公电话
	private String ozipCode; //办公邮编
	private String oaddres;//办公地址
	private List<String> roleNames;	//所拥有的角色
	private int realLock;//账号是否被锁定
	private String picture;
	
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

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgSeq() {
		return orgSeq;
	}

	public void setOrgSeq(String orgSeq) {
		this.orgSeq = orgSeq;
	}

	public long getAreaOrgId() {
		return areaOrgId;
	}

	public void setAreaOrgId(long areaOrgId) {
		this.areaOrgId = areaOrgId;
	}

	public long getRootOrgId() {
		return rootOrgId;
	}

	public void setRootOrgId(long rootOrgId) {
		this.rootOrgId = rootOrgId;
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

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getOtel() {
		return otel;
	}

	public void setOtel(String otel) {
		this.otel = otel;
	}

	public String getOzipCode() {
		return ozipCode;
	}

	public void setOzipCode(String ozipCode) {
		this.ozipCode = ozipCode;
	}

	public String getOaddres() {
		return oaddres;
	}

	public void setOaddres(String oaddres) {
		this.oaddres = oaddres;
	}

	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	public int getRealLock() {
		return realLock;
	}

	public void setRealLock(int realLock) {
		this.realLock = realLock;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
}
