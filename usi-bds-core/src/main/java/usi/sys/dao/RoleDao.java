package usi.sys.dao;

import java.util.List;

import usi.sys.dto.PageObj;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;

public interface RoleDao {
	/**
	 * 分页查找出所有的角色
	 * @return
	 */
	public List<Role> queryAllRoles(Role role);
	
	/**
	 * 查询角色的总记录数
	 * @return
	 */
	public int queryAllRolesCount();
	
	/**
	 * 插入角色
	 * @param role
	 */
	public void insertRole(Role role);
	
	/**
	 * 根据角色ID修改角色
	 * @param role
	 */
	public void updateRole(Role role);
	
	/**
	 * 删除角色的菜单权限
	 * @param role
	 */
	public void deleteRoleMenus(Role role);
	
	/**
	 * 批量添加角色的菜单权限
	 * @param role
	 */
	public void batchSaveRoleMenus(final Role role);
	
	/**
	 * 根据角色ID查询该角色下所有的人员
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	public List<StaffInfo> queryPageStaffsByRoleId(PageObj pageObj, Staff staff, String roleId);
	
	/**
	 * 批量删除角色下的成员
	 * @param role
	 */
	public void batchDeleteRoles(final Role role);
	
	/**
	 * 根据角色ID查询出所有的未添加进该角色的员工,用于选择添加
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	public List<StaffInfo> queryPageAllOtherStaffsByRoleId(PageObj pageObj, Staff staff, String roleId);
	
	/**
	 * 批量添加角色的成员
	 * @param role
	 */
	public void batchSaveRoleStaffs(final  Role role);
	
	/**
	 * 删除角色(逻辑删除)
	 * @param roleId
	 */
	public void deleteRoleById(Integer roleId);

	/**
	 * 查询某角色编码的个数
	 * @param roleCode
	 * @return
	 */
	public int getRoleCodeCount(String roleCode);
}
