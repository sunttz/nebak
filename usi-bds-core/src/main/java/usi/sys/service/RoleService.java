package usi.sys.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dao.RoleDao;
import usi.sys.dto.PageObj;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;

@Service
@Transactional
public class RoleService {
	
	@Resource
	private RoleDao roleDao;
//	@Resource
//	private MenuRolesHolder menuRolesHolder;
	
	/**
	 * 查询出所有的角色
	 * @return
	 */
	public List<Role> getAllRoles(Role role) {
		return roleDao.queryAllRoles(role);
	}
	
	/**
	 * 保存角色
	 * @param role
	 */
	public void saveRole(Role role) {
		roleDao.insertRole(role);
	}
	
	/**
	 * 修改角色
	 * @param role
	 */
	public void updateRole(Role role) {
		roleDao.updateRole(role);
	}
	
	/**
	 * 保存角色的菜单关系
	 * @param role
	 */
	public void updateRoleMenus(Role role) {
		roleDao.deleteRoleMenus(role);
		roleDao.batchSaveRoleMenus(role);
//		//更新系统维护的全局变量(菜单及其对应的角色列表)
//		menuRolesHolder.updateMenuRoles();
	}
	
	/**
	 * 根据角色ID查询该角色下的成员
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	public List<StaffInfo> getStaffsByRoleId(PageObj pageObj, Staff staff, String roleId) {
		return roleDao.queryPageStaffsByRoleId(pageObj, staff, roleId);
	}
	
	/**
	 * 批量删除角色下的成员
	 * @param role
	 */
	public void batchRemoveRoleStaffs(Role role) {
		roleDao.batchDeleteRoles(role);
	}
	
	/**
	 * 根据角色ID查询出所有的未添加进该角色的员工,用于选择添加
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	public List<StaffInfo> getAllOtherEmps(PageObj pageObj, Staff staff,String roleId) {
		return roleDao.queryPageAllOtherStaffsByRoleId(pageObj, staff, roleId);
	}
	
	/**
	 * 批量添加角色的成员
	 * @param role
	 */
	public void batchSaveRoleStaffs(Role role) {
		roleDao.batchSaveRoleStaffs(role);
	}
	
	/**
	 * 删除角色,先删除角色下的菜单
	 * @param role
	 */
	public void removeRole(Role role) {
		roleDao.deleteRoleMenus(role);
		roleDao.deleteRoleById(role.getRoleId());
//		//更新系统维护的全局变量(菜单及其对应的角色列表)
//		menuRolesHolder.updateMenuRoles();
	}

	/**
	 * 检查角色编码是否存在
	 * @param roleCode
	 * @return
	 */
	public boolean roleCodeisExist(String roleCode) {
		int count = roleDao.getRoleCodeCount(roleCode);
		if(count == 0) {
			return false;
		}
		return true;
	}
}
