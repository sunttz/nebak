package usi.sys.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import usi.sys.dao.StaffDao;
import usi.sys.dto.AuthInfo;
import usi.sys.dto.AuthMenu;
import usi.sys.dto.PageObj;
import usi.sys.dto.RoleDto;
import usi.sys.dto.RoleGrantInfo;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Staff;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;
import usi.util.PropertyUtil;

@Service
public class StaffService {

	@Resource
	private StaffDao staffDao;
	
	/**
	 * 分页查询员工
	 * @param pageObj
	 * @param staff
	 * @return
	 */
	public Map<String,Object> queryPageStaffByCondition(PageObj pageObj, Staff staff){
		Map<String,Object> map = new HashMap<String,Object>();
		List<StaffInfo> list = staffDao.queryPageStaffByCondition(pageObj, staff);
		map.put("total", pageObj.getTotal());
		map.put("rows", list);
		return map;
	}
	
	/**
	 * 根据主键加载员工详细信息
	 * @param staffId
	 * @return
	 */
	@Transactional(rollbackFor=Exception.class)
	public StaffInfo queryStaffDetailById(Long staffId){
		return staffDao.queryStaffDetailById(staffId);
	}
	
	/**
	 * 根据主键查询员工简略信息,主键，所属机构主键，机构序列，姓名，工号
	 * @param staffId
	 * @return
	 */
	public StaffInfo queryStaffById(Long staffId){
		return staffDao.queryStaffById(staffId);
	}
	
	/**
	 * 逻辑删除员工信息
	 * @param staffIds
	 */
	@Transactional(rollbackFor=Exception.class)
	public void batchDelStaff(final Long[] staffIds){
		staffDao.batchDelStaff(staffIds);
		staffDao.batchDelStaffRoleRel(staffIds);
	}
	
	/**
	 * 检查登录帐号是否存在
	 * @param userId
	 * @return
	 */
	public int checkUserId(String userId){
		return staffDao.checkUserId(userId);
	}
	
	/**
	 * 新增员工信息
	 * @param staff
	 */
	@Transactional(rollbackFor=Exception.class)
	public void addStaff(Staff staff){
		//密码加盐加密
		String password = CommonUtil.getMd5(staff.getUserId()+ConstantUtil.DEFAULT_PASSWORD);
		staff.setPassword(password);
		Long staffId = staffDao.insertStaffMain(staff);
		staff.setStaffId(staffId);
		staffDao.insertStaffAttr(staff);
	}
	
	/**
	 * 保存员工头像图片
	 * @param logo
	 * @return
	 */
	public String uploadPhoto(MultipartFile logo){
		String filename = logo.getOriginalFilename();
		String extName = filename.substring(filename.lastIndexOf(".")+1).toLowerCase();
		String path= PropertyUtil.getStringValue("upload.img.path");
		//文件名处理
		String lastName = System.currentTimeMillis()+"."+extName;
		//保存路径
		File fileDir =  new File(path);
		if(!fileDir.exists()){
		    fileDir.mkdirs();
		}
		try {
			if (extName.matches("gif|jpg|jpeg|png|bmp")) {
				//保存文件
				FileCopyUtils.copy(logo.getBytes(),new File(fileDir,lastName));
				return path + "/" + lastName;
			}else{
				return "ERROR PHOTO";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
			
		} 
	}
	
	/**
	 * 更新员工信息
	 * @param staff
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateStaff(Staff staff){
		staffDao.updateStaffMain(staff);
		staffDao.updateStaffAttr(staff);
	}
	
	/**
	 * 重置密码
	 * @param staff
	 */
	@Transactional(rollbackFor=Exception.class)
	public void resetPwd(Staff staff){
		//密码加盐加密
		String password = CommonUtil.getMd5(staff.getUserId()+ConstantUtil.DEFAULT_PASSWORD);
		staff.setPassword(password);
		staffDao.resetPwd(staff);
	}
	
	/**
	 * 分页查询角色
	 * @param pageObj
	 * @param roleGrantInfo
	 * @return
	 */
	public Map<String, Object> queryPageRole(PageObj pageObj, RoleGrantInfo roleGrantInfo){
		Map<String,Object> map = new HashMap<String,Object>();
		List<RoleGrantInfo> list = staffDao.queryPageRole(pageObj, roleGrantInfo);
		map.put("total", pageObj.getTotal());
		map.put("rows", list);
		return map;
	}
	
	/**
	 * 取消对员工授予角色
	 * @param staffId
	 * @param roleIds
	 */
	@Transactional(rollbackFor=Exception.class)
	public void batchRevokeRole(final Long staffId, final Integer[] roleIds){
		staffDao.batchRevokeRole(staffId, roleIds);
	}
	
	/**
	 * 对员工授予角色
	 * @param staffId
	 * @param roleIds
	 */
	@Transactional(rollbackFor=Exception.class)
	public void batchGrantRole(final Long staffId, final Integer[] roleIds){
		staffDao.batchGrantRole(staffId, roleIds);
	}
	
	/**
	 * 登录认证
	 * @param authInfo
	 * @return
	 */
	public Map<String, Object> validateLogin(AuthInfo authInfo) {
		
		Map<String, Object> msgMap = new HashMap<String, Object>();
		
		try {
			String userId = authInfo.getUserId();
			String password = authInfo.getPassword();
			StaffInfo staff = staffDao.queryStaffByUserId(userId);
			if (staff == null) {
				msgMap.put("msgFlag", false);
				//故意不提示用户名错误，防止恶意访问
				msgMap.put("msgDesc", "用户名或者密码错误！");
				return msgMap;
			}
			if(staff.getRealLock()==1) {
				msgMap.put("msgFlag", false);
				msgMap.put("msgDesc", "账户被锁定！");
				return msgMap;
			}
			if (!password.equals(staff.getPassword())) {
				msgMap.put("msgFlag", false);
				//故意不提示密码错误，防止恶意访问
				msgMap.put("msgDesc", "用户名或者密码错误！");
				//更新输入密码错误次数
				staffDao.uptErrPwdTimes(userId);
				return msgMap;
			}
			
			//认证成功后，将相关信息装配进AuthInfo对象
			authInfo.setStaffId(staff.getStaffId());
			authInfo.setMobileNbr(staff.getMobileNum());
			authInfo.setOrgSeq(staff.getOrgSeq());
			authInfo.setAreaOrgId(staff.getAreaOrgId());
			authInfo.setRootOrgId(staff.getRootOrgId());
			authInfo.setOrgId(staff.getOrgId());
			authInfo.setOrgName(staff.getOrgName());
			authInfo.setUserName(staff.getOperatorName());
			authInfo.setPicture(staff.getPicture());
			
			//取角色列表（这里是对象）
			List<RoleDto> roleList = getRoleByUserId(staff.getStaffId());
			authInfo.setRoles(roleList);
			msgMap.put("msgFlag", true);
			msgMap.put("msgDesc", "登录成功！");
			return msgMap;
		} catch (Exception e) {
			e.printStackTrace();
			msgMap.put("msgFlag", false);
			msgMap.put("msgDesc", "系统异常！");
			return msgMap;
		}
	}
	
	/**
	 * 根据staffId查询角色id列表
	 * @param staffId
	 * @return
	 */
	public List<RoleDto> getRoleByUserId(long staffId) {
		return staffDao.getRoleByUserId(staffId);
	}
	/**
	 * 得到用户的权限菜单
	 * @param userId
	 * @return
	 */
	public List<AuthMenu> getAuthMenusByUserId(String userId) {
		List<AuthMenu> menus = staffDao.queryAuthMenusByUserId(userId);
		AuthMenu pMenu = new AuthMenu();
		pMenu.setMenuId(0);
		pMenu.setParentId(-1);
		menus.add(pMenu);
		for(AuthMenu menu: menus) {
			for(AuthMenu menu2: menus) {
				if(menu2.getParentId() == menu.getMenuId()) {
					menu.getChildren().add(menu2);
				}
			}
		}
		return pMenu.getChildren();
	}
	
	/**
	 * 得到用户的权限菜单(叶子菜单)
	 * @param userId
	 * @return
	 */
	public Map<String,String> getLeafAuthMenusByUserId(String userId){
		//存放叶子菜单的map（放在session里，做菜单权限校验）
		Map<String,String> leafMenusMap = new HashMap<String,String>();
		List<AuthMenu> menus = staffDao.queryAuthMenusByUserId(userId);
		for(AuthMenu menu: menus) {
			//如果是叶子菜单，放到map里
			if(menu.getIsLeaf() == 1) {
				String menuaction = menu.getMenuAction();
				//问号后面都去掉
				if(menuaction.indexOf("?")>-1) {
					menuaction = menuaction.substring(0,menuaction.indexOf("?"));
				}
				leafMenusMap.put(menuaction, "1");
			}
		}
		return leafMenusMap;
	}
	
	/**
	 * 修改密码
	 * @param userId
	 * @param pwd 用户正确的原密码
	 * @param oldPwd 用户输入的原密码
	 * @param newPwd 用户输入的新密码
	 * @return
	 */
	public Map<String, Object> updatePwd(long staffId, String pwd, String oldPwd, String newPwd) {
		Map<String, Object> magMap = new HashMap<String, Object>();
		if(!pwd.equals(oldPwd)) {
			magMap.put("msgFlag", false);
			magMap.put("msgDesc", "旧的密码不正确！");
		} else {
			if(staffDao.updatePwdById(staffId, newPwd)==1) {
				magMap.put("msgFlag", true);
				magMap.put("msgDesc", "密码修改成功！");
			} else {
				magMap.put("msgFlag", false);
				magMap.put("msgDesc", "系统异常，稍后重试！");
			}
		}
		return magMap;
	}
	
	/**
	 * 查询员工角色和机构序列信息
	 * @param staffId
	 * @return
	 */
	public StaffInfo getStaffRoleInfo(long staffId){
		return staffDao.getStaffRoleInfo(staffId);
	}
}
