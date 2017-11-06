package usi.sys.dto;

import java.io.Serializable;

/**
 * @Description AuthInfo需要的角色dto
 * @author zhang.dechang
 * @date 2015年8月1日 下午5:45:47
 */
public class RoleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	//角色id
	private Integer roleId;
	
	//角色名称
	private String roleName;
	
	/** 角色编码*/
	private String roleCode;
	
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
	
}
