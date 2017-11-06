package usi.sys.dao;

import java.util.List;

import usi.sys.dto.AuthMenu;
import usi.sys.dto.PageObj;
import usi.sys.dto.RoleDto;
import usi.sys.dto.RoleGrantInfo;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Staff;

public interface StaffDao {
	
	/**
	 * 分页查询机构员工
	 * @param pageObj
	 * @param staff
	 * @return
	 */
	public List<StaffInfo> queryPageStaffByCondition(PageObj pageObj, Staff staff);
	
	/**
	 * 根据登录账号查询用户信息
	 * @param userId
	 * @return
	 */
	public StaffInfo queryStaffByUserId(String userId);
	
	/**
	 * 根据userid更新输入密码错误次数
	 * @param userId
	 * @return
	 * 备注：上次输入错误时间距离当前大于10分钟或者错误次数已经达到3次，更新为1次
	 * 上次输入错误时间距离当前10分钟且已达到2次（加上本次为3次），则锁定，否则不锁定
	 */
	public int uptErrPwdTimes(String userId);
	
	/**
	 * 根据主键查询员工详细信息
	 * @param staffId
	 * @return
	 */
	public StaffInfo queryStaffDetailById(Long staffId);
	
	/**
	 * 根据 主键查询员工简略信息
	 * @param staffId
	 * @return
	 */
	public StaffInfo queryStaffById(Long staffId);
	
	/**
	 * 批量删除员工
	 * @param staffIds
	 */
	public void batchDelStaff(final Long[] staffIds);
	
	/**
	 * 删除员工角色关系
	 * @param staffIds
	 */
	public void batchDelStaffRoleRel(final Long[] staffIds);
	
	/**
	 * 取消对员工授予角色
	 * @param staffId
	 * @param roleIds
	 */
	public void batchRevokeRole(final Long staffId, final Integer[] roleIds);
	
	/**
	 * 对员工授予角色
	 * @param staffId
	 * @param roleIds
	 */
	public void batchGrantRole(final Long staffId, final Integer[] roleIds);
	
	/**
	 * 保存新增加的员工主要信息，返回主键
	 * @param staff
	 * @return
	 */
	public Long insertStaffMain(final Staff staff);
	
	/**
	 * 保存新增加员工的属性信息
	 * @param staff
	 */
	public void insertStaffAttr(final Staff staff);
	
	/**
	 * 更新员工主要信息
	 * @param staff
	 */
	public void updateStaffMain(final Staff staff);
	
	/**
	 * 重置员工登录密码
	 * @param staff
	 */
	public void resetPwd(final Staff staff);
	
	/**
	 * 更新员工属性信息
	 * @param staff
	 */
	public void updateStaffAttr(final Staff staff);
	
	/**
	 * 分页查询所有角色，包括对当前员工的授予信息
	 * @param pageObj
	 * @param roleGrantInfo
	 * @return
	 */
	public List<RoleGrantInfo> queryPageRole(PageObj pageObj, final RoleGrantInfo roleGrantInfo);
	
	/**
	 * 检查登录帐号是否存在
	 * @param userId
	 * @return
	 */
	public int checkUserId(String userId);
	
	/**
	 * 根据staffId查询角色id列表
	 * @param staffId
	 * @return
	 */
	public List<RoleDto> getRoleByUserId(long staffId);
	/**
	 * 根据登录账号查询改用户的菜单权限
	 * @param userId
	 * @return
	 */
	public List<AuthMenu> queryAuthMenusByUserId(String userId);
	
	/**
	 * 修改密码
	 * @param userId
	 * @param newPwd
	 * @return
	 */
	public int updatePwdById(long staffId, String newPwd);
	
	/**
	 * 查询员工角色和机构序列信息
	 * @param staffId
	 * @return
	 */
	public StaffInfo getStaffRoleInfo(long staffId);
}
