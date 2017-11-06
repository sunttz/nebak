package usi.sys.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dao.SysOptLogDao;
import usi.sys.entity.BusiDict;
import usi.sys.entity.Menu;
import usi.sys.entity.Org;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;
import usi.sys.entity.SysOptLog;
import usi.sys.entity.SysScene;
import usi.sys.util.ConstantUtil;

@Service
public class SysOptLogService {
	
	@Resource
	private SysOptLogDao sysOptLogDao;
	
	/**
	 * @Description 用户试图登陆
	 * @param ip
	 * @param userId
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void doLogin(String ip,String userId){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("系统登陆");
		sysOptLog.setOptName(ConstantUtil.OPT_LOGIN);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("用户登陆");
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：业务字典新增业务场景
	 * @param ip
	 * @param userId
	 * @param sysScene
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void insertSysScene(String ip,String userId,SysScene sysScene){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("业务字典管理");
		sysOptLog.setOptName(ConstantUtil.OPT_SYS_SCENE_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(sysScene.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：删除业务场景
	 * @param ip
	 * @param userId
	 * @param sysScene
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void deleteSysScene(String ip,String userId,SysScene beforeDel){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("业务字典管理");
		sysOptLog.setOptName(ConstantUtil.OPT_SYS_SCENE_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(beforeDel.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：修改业务场景
	 * @param ip
	 * @param userId
	 * @param sysScene
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateSysScene(String ip,String userId,SysScene beforeUpdate,SysScene sysScene){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("业务字典管理");
		sysOptLog.setOptName(ConstantUtil.OPT_SYS_SCENE_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("修改前："+beforeUpdate.toString()+"\n 修改信息："+sysScene.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：删除业务字典项
	 * @param ip
	 * @param userId
	 * @param dicId
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void deleteBusiDict(String ip,String userId,BusiDict beforeDel){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("业务字典管理");
		sysOptLog.setOptName(ConstantUtil.OPT_DICT_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(beforeDel.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：修改业务字典项
	 * @param ip
	 * @param userId
	 * @param busiDict
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateBusiDict(String ip,String userId,BusiDict beforeUpdate,BusiDict busiDict){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("业务字典管理");
		sysOptLog.setOptName(ConstantUtil.OPT_DICT_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("修改前："+beforeUpdate.toString()+"\n 修改信息："+busiDict.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：新增字典项
	 * @param ip
	 * @param userId
	 * @param busiDict
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void insertBusiDict(String ip,String userId,BusiDict busiDict){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("业务字典管理");
		sysOptLog.setOptName(ConstantUtil.OPT_DICT_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(busiDict.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：新增菜单
	 * @param ip
	 * @param userId
	 * @param menu
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void insertMenu(String ip,String userId,Menu menu){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("菜单管理");
		sysOptLog.setOptName(ConstantUtil.OPT_MENU_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(menu.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description	操作：删除菜单
	 * @param ip
	 * @param userId
	 * @param menuId
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void deleteMenu(String ip,String userId,Menu beforeDel){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("菜单管理");
		sysOptLog.setOptName(ConstantUtil.OPT_MENU_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(beforeDel.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：修改菜单
	 * @param ip
	 * @param userId
	 * @param menu
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateMenu(String ip,String userId,Menu beforeUpdate,Menu menu){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("菜单管理");
		sysOptLog.setOptName(ConstantUtil.OPT_MENU_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("修改前："+beforeUpdate.toString()+"\n 修改信息："+menu.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：添加角色
	 * @param ip
	 * @param userId
	 * @param role
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void insertRole(String ip,String userId,Role role){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("角色管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ROLE_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(role.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：修改角色信息
	 * @param ip
	 * @param userId
	 * @param role
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateRole(String ip,String userId,Role beforeUpdate,Role role){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("角色管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ROLE_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("修改前："+beforeUpdate.toString()+"\n 修改信息："+role.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：删除角色(将会连带删除该角色对)
	 * @param ip
	 * @param userId
	 * @param roleId
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void removeRole(String ip,String userId,Role beforeDel){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("角色管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ROLE_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(beforeDel.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：更新角色拥有的菜单
	 * @param ip
	 * @param userId
	 * @param role
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateRoleMenus(String ip,String userId,Role beforeUpdate,Role role){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("角色管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ROLE_MENUS_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("更新前："+beforeUpdate.toString()+"\n 更新后："+role);
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：删除角色下的人员
	 * @param ip
	 * @param userId
	 * @param role
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void removeRoleStaffs(String ip,String userId,Role role){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("角色管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ROLE_STAFF_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(role.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：添加角色下的人员
	 * @param ip
	 * @param userId
	 * @param role
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void addRoleStaffs(String ip,String userId,Role role){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("角色管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ROLE_STAFF_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(role.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：添加机构
	 * @param ip
	 * @param userId
	 * @param org
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void insertOrg(String ip,String userId,Org org){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ORG_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(org.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：修改机构
	 * @param ip
	 * @param userId
	 * @param org
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateOrg(String ip,String userId,Org beforeUpdate,Org org){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ORG_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("更新前："+beforeUpdate.toString()+"\n 更新信息："+org.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：删除机构
	 * @param ip
	 * @param userId
	 * @param orgId
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void deleteOrg(String ip,String userId,Org beforeDel){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_ORG_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(beforeDel.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：删除人员信息
	 * @param ip
	 * @param userId
	 * @param staffIds
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void delStaff(String ip,String userId,Long[] staffIds){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_STAFF_DELETE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(Arrays.toString(staffIds));
		sysOptLogDao.insertLog(sysOptLog);
	}

	/**
	 * @Description 操作：修改员工信息
	 * @param ip
	 * @param userId
	 * @param beforeUpdate
	 * @param staff
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void updateStaff(String ip,String userId,Staff beforeUpdate,Staff staff){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_STAFF_UPDATE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent("更新前："+beforeUpdate.toString()+"\n 更新信息："+staff.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：增加人员信息
	 * @param ip
	 * @param userId
	 * @param staff
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void addStaff(String ip,String userId,Staff staff){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_STAFF_INSERT);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(staff.toString());
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：给员工赋予角色
	 * @param ip
	 * @param userId
	 * @param roleIds
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void staffGrantRole(String ip,String userId, Integer[] roleIds){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_STAFF_ROLES_ADD);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(Arrays.toString(roleIds));
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	/**
	 * @Description 操作：取消员工角色
	 * @param ip
	 * @param userId
	 * @param roleIds
	 */
	@Async
	@Transactional(rollbackFor=Exception.class)
	public void staffRevokeRole(String ip,String userId, Integer[] roleIds){
		SysOptLog sysOptLog = new SysOptLog();
		sysOptLog.setOptIp(ip);
		sysOptLog.setOptModule("机构人员管理");
		sysOptLog.setOptName(ConstantUtil.OPT_STAFF_ROLES_REMOVE);
		sysOptLog.setUserId(userId);
		sysOptLog.setOptContent(Arrays.toString(roleIds));
		sysOptLogDao.insertLog(sysOptLog);
	}
	
	@Transactional(readOnly=true)
	public Org getOrgByOrgId(Long orgId){
		return sysOptLogDao.getOrgByOrgId(orgId);
	}
	@Transactional(readOnly=true)
	public BusiDict getBusiDictById(Long dicId){
		return sysOptLogDao.getBusiDictById(dicId);
	}
	@Transactional(readOnly=true)
	public SysScene getSysSceneByCode(String busiSceneCode){
		return sysOptLogDao.getSysSceneByCode(busiSceneCode);
	}
	@Transactional(readOnly=true)
	public Menu getMenuByMenuId(Long menuId){
		return sysOptLogDao.getMenuByMenuId(menuId);
	}
	@Transactional(readOnly=true)
	public Role getRoleByRoleId(Integer roleId){
		return sysOptLogDao.getRoleByRoleId(roleId);
	}
	@Transactional(readOnly=true)
	public List<Long> getRoleMenusByRoleId(Integer roleId){
		return sysOptLogDao.getRoleMenusByRoleId(roleId);
	}
	@Transactional(readOnly=true)
	public Staff getStaffByStaffId(Long staffId){
		return sysOptLogDao.getStaffByStaffId(staffId);
	}
	
}
