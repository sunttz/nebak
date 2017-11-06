package usi.sys.dao;

import java.util.List;

import usi.sys.entity.BusiDict;
import usi.sys.entity.Menu;
import usi.sys.entity.Org;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;
import usi.sys.entity.SysOptLog;
import usi.sys.entity.SysScene;

/**
 * @Description 记录操作日志需要的相关数据库操作
 * @author zhang.dechang
 * @date 2014年12月20日 下午6:19:06
 */
public interface SysOptLogDao {
	
	/**
	 * @Description 保存操作日志
	 * @param sysOptLog
	 */
	public void insertLog(SysOptLog sysOptLog);
	
	/**
	 * @Description 根据ID查询字典项
	 * @param dicId
	 * @return
	 */
	public BusiDict getBusiDictById(Long dicId);
	
	/**
	 * @Description 根据编码查询场景信息
	 * @param busiSceneCode
	 * @return
	 */
	public SysScene getSysSceneByCode(String busiSceneCode);
	
	/**
	 * @Description 根据menuId查询菜单信息
	 * @param menuId
	 * @return
	 */
	public Menu getMenuByMenuId(Long menuId);
	
	/**
	 * @Description 根据roleId查询角色信息
	 * @param roleId
	 * @return
	 */
	public Role getRoleByRoleId(Integer roleId);
	
	/**
	 * @Description 根据roleId查询其拥有的菜单(返回菜单ID字段)
	 * @param roleId
	 * @return
	 */
	public List<Long> getRoleMenusByRoleId(Integer roleId);
	
	/**
	 * @Description 根据orgId查询机构信息
	 * @param orgId
	 * @return
	 */
	public Org getOrgByOrgId(Long orgId);
	
	/**
	 * @Description 根据staffId查询人员信息
	 * @param staffId
	 * @return
	 */
	public Staff getStaffByStaffId(Long staffId);
}
