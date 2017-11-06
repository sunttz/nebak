package usi.sys.dto;

public class RoleGrantInfo {
	private Integer roleId; //角色ID
	private String roleName; //角色名称
	private String roleCode; //角色编码
	private String roleDesc; //角色说明
	private Integer isGranted; //是否授予 0已授予 1未授予
	private Long staffId; //员工ID
	
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
	public Integer getIsGranted() {
		return isGranted;
	}
	public void setIsGranted(Integer isGranted) {
		this.isGranted = isGranted;
	}
	public Long getStaffId() {
		return staffId;
	}
	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
	
}
