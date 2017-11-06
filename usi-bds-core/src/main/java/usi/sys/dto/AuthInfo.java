package usi.sys.dto;

import java.io.Serializable;
import java.util.List;

import usi.sys.util.JacksonUtil;

/**
 * 登录信息
 * @author fan.fan
 * @date 2014-3-27 下午1:50:38
 */
public class AuthInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	//用户主键
	private long staffId;
	
	//登录名
	private String userId;
	
	//员工姓名
	private String userName;
	
	//机构id
	private long orgId;
	
	//机构序列
	private String orgSeq;
	
	//机构名称
	private String orgName;
	
	//密码
	private String password;
	
	//手机号码
	private String mobileNbr;
	
	//本地网机构id
	private long areaOrgId; 
	
	//根机构ID（省公司机构id）
	private long rootOrgId;
	
	//拥有的角色
	private List<RoleDto> roles;
	
	//头像图片
	private String picture;
	
	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	public String getMobileNbr() {
		return mobileNbr;
	}

	public void setMobileNbr(String mobileNbr) {
		this.mobileNbr = mobileNbr;
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

	public List<RoleDto> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDto> roles) {
		this.roles = roles;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		String result = "";
		try {
			result = JacksonUtil.obj2json(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
