package usi.sys.entity;

import java.util.List;

import usi.sys.util.JacksonUtil;

public class Role {
	
	/**
	 * 角色ID
	 */
	private Integer roleId;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	/**
	 * 角色编码
	 */
	private String roleCode;
	
	/**
	 * 角色说明
	 */
	private String roleDesc;
	
	/**
	 * 状态
	 */
	private Integer state;
	
	/**
	 * 角色下的成员数量
	 */
	private Integer staffSize;
	
	/**
	 * 当前角色能够查看的菜单
	 */
	private List<Long> menuIds;
	
	/**
	 * 角色下包含的人员
	 */
	private List<Long> staffIds;
	
	@Override
	public String toString(){
		String result = "";
		try {
			result = JacksonUtil.obj2json(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getStaffSize() {
		return staffSize;
	}

	public void setStaffSize(Integer staffSize) {
		this.staffSize = staffSize;
	}

	public List<Long> getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(List<Long> menuIds) {
		this.menuIds = menuIds;
	}

	public List<Long> getStaffIds() {
		return staffIds;
	}

	public void setStaffIds(List<Long> staffIds) {
		this.staffIds = staffIds;
	}
	
}
