package usi.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.PageObj;
import usi.sys.dto.StaffInfo;
import usi.sys.entity.Role;
import usi.sys.entity.Staff;
import usi.sys.service.MenuService;
import usi.sys.service.RoleService;
import usi.sys.service.SysOptLogService;
import usi.sys.util.ConstantUtil;
import usi.sys.util.IpAddressUtil;

/**
 * @author huang.junmei
 * 创建时间：2014-3-26 下午2:43:09
 */
@Controller
@RequestMapping("/role")
public class RoleController {

	@Resource
	private RoleService roleService;
	@Resource
	private MenuService menuService;
	@Resource
	private SysOptLogService sysOptLogService;
	
	/**
	 * 角色管理主界面
	 * @return
	 */
	@RequestMapping(value = "/main.do",method =RequestMethod.GET)
	public String getMain() {
		return "system/role";
	}
	
	/**
	 * 查询出所有的角色
	 * @return
	 */
	@RequestMapping(value = "/getRoles.do",method =RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getRoles(Role role) {
		List<Role> roles = roleService.getAllRoles(role);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("total", roles.size());
		modelMap.put("rows", roles);
		return modelMap;
	}
	
	/**
	 * 查询菜单树
	 * @return
	 */
	@RequestMapping(value = "/getMenuTree.do",method =RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> getMenuTree() {
		return menuService.getMenus();
	}
	
	/**
	 * 根据角色ID查询该角色下的成员
	 * @param pageObj
	 * @param staff
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/getStaffs.do")
	@ResponseBody
	public Map<String, Object> getStaffs(PageObj pageObj, Staff staff, String roleId) {
		List<StaffInfo> emps = roleService.getStaffsByRoleId(pageObj, staff, roleId);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("total", pageObj.getTotal());
		modelMap.put("rows", emps);
		return modelMap;
	}
	
	/**
	 * 根据角色ID查询出所有的未添加进该角色的员工,用于选择添加
	 * @param pageObj
	 * @param Param
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/getAllOtherStaffs.do")
	@ResponseBody
	public Map<String, Object> getAllOtherStaffs(PageObj pageObj, Staff param, String roleId) {
		List<StaffInfo> emps = roleService.getAllOtherEmps(pageObj, param, roleId);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("total", pageObj.getTotal());
		modelMap.put("rows", emps);
		return modelMap;
	}
	
	/**
	 * 增加,修改角色
	 * @param role
	 * @param pw
	 */
	@RequestMapping(value = "/postRole.do",method =RequestMethod.POST)
	@ResponseBody
	public String postRole(HttpServletRequest request,HttpSession session,Role role) {
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			//检查角色编码是否存在
			boolean exist = roleService.roleCodeisExist(role.getRoleCode());
			if(exist){
				return "exist";
			}
			if(role.getRoleId() == -1) {
				role.setState(1);
				roleService.saveRole(role);
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.insertRole(IpAddressUtil.getReqIp(request),userId,role);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Role beforeUpdate = null;
				if(ConstantUtil.WRITELOG){
					beforeUpdate = sysOptLogService.getRoleByRoleId(role.getRoleId());
				}
				roleService.updateRole(role);
				if(ConstantUtil.WRITELOG){
					try {
						sysOptLogService.updateRole(IpAddressUtil.getReqIp(request),userId,beforeUpdate,role);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 删除角色
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/removeRole.do", method = RequestMethod.POST)
	@ResponseBody
	public String removeRole(HttpServletRequest request,HttpSession session,Role role) {
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			Role beforeDel = null;
			if(ConstantUtil.WRITELOG){
				beforeDel = sysOptLogService.getRoleByRoleId(role.getRoleId());
			}
			roleService.removeRole(role);
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.removeRole(IpAddressUtil.getReqIp(request),userId,beforeDel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 保存角色的菜单关系
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/postMenus.do", method = RequestMethod.POST)
	@ResponseBody
	public String postMenus(HttpServletRequest request,HttpSession session,@RequestBody Role role) {
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			Role beforeUpdate = null;
			if(ConstantUtil.WRITELOG){
				List<Long> menus = sysOptLogService.getRoleMenusByRoleId(role.getRoleId());
				beforeUpdate = new Role();
				beforeUpdate.setRoleId(role.getRoleId());
				beforeUpdate.setMenuIds(menus);
			}
			roleService.updateRoleMenus(role);
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.updateRoleMenus(IpAddressUtil.getReqIp(request),userId,beforeUpdate,role);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 删除角色下的成员
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/removeStaffs.do", method = RequestMethod.POST)
	@ResponseBody
	public String removeStaffs(HttpServletRequest request,HttpSession session,@RequestBody Role role) {
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			roleService.batchRemoveRoleStaffs(role);
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.removeRoleStaffs(IpAddressUtil.getReqIp(request),userId,role);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 添加角色下的成员
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/postStaffs.do", method = RequestMethod.POST)
	@ResponseBody
	public String postStaffs(HttpServletRequest request,HttpSession session,@RequestBody Role role) {
		try {
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			roleService.batchSaveRoleStaffs(role);
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.addRoleStaffs(IpAddressUtil.getReqIp(request),userId,role);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}
