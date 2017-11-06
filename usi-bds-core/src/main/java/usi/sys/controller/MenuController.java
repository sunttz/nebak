package usi.sys.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.MenuDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.Menu;
import usi.sys.service.MenuService;
import usi.sys.service.SysOptLogService;
import usi.sys.util.ConstantUtil;
import usi.sys.util.IpAddressUtil;
/**
 * 
 * @author lmwang
 * 创建时间：2014-3-26 下午2:42:40
 */
@Controller
@RequestMapping("/menu")
public class MenuController {
	
	@Resource
	private MenuService menuService;
	
	@Resource
	private SysOptLogService sysOptLogService;
	
	/**
	 * 菜单管理主界面
	 * @return
	 */
	@RequestMapping(value = "/main.do",method =RequestMethod.GET)
	public String getMain() {
		return "system/menu";
	}
	
	/**
	 * 查询菜单树
	 * @return
	 */
	@RequestMapping(value = "/getMenuTree.do")
	@ResponseBody
	public List<MenuDto> getMenuTree(String id) {
		//加载根节点时，无id传入
		String menuId = id == null ? "-1" : id;
		return menuService.getMenuTree(menuId);
	}
	
	/**
	 * 分页查询所有菜单信息
	 */
	@RequestMapping(value = "/queryPageMenus.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryMenusByPage(PageObj pageObj,String menuName,String menuLevel,String seq){
		String name = menuName;
		String level = menuLevel;
		String sequence = seq;
		if(menuName !=null && "".equals(menuName) ){
			name = null;
		}
		if(menuLevel != null && "".equals(menuLevel)){
			level = null;
		}
		if(seq != null && "".equals(seq)){
			sequence = null;
		}
		Map<String, Object> modelMap =  menuService.queryMenusByPage(pageObj, name, level,sequence);
		return modelMap;
	}
	/**
	 * 向菜单表中插入数据
	 * @param menu
	 * @param pw
	 */
	@RequestMapping(value = "/insertMenu.do", method = RequestMethod.POST)
	public void insertMenu(HttpServletRequest request,HttpSession session,PrintWriter pw,Menu menu){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			menu.setStatus(1);
			menuService.insertMenu(menu);
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.insertMenu(IpAddressUtil.getReqIp(request),userId,menu);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.print("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.print("fail");
		}
	}
	/**
	 * 删除菜单表中的数据
	 * @param seq
	 * @param pw
	 */
	@RequestMapping(value = "/deleteMenu.do", method = RequestMethod.POST)
	@ResponseBody
	public String deleteMenu(HttpServletRequest request,HttpSession session,Menu menu){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			Menu beforeDel = null;
			if(ConstantUtil.WRITELOG){
				beforeDel = sysOptLogService.getMenuByMenuId(menu.getMenuId());
			}
			menuService.deleteMenu(menu.getMenuId());
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.deleteMenu(IpAddressUtil.getReqIp(request),userId,beforeDel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "success";
		}catch(Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	/**
	 * 更新菜单表中的数据
	 * @param menu
	 * @param pw
	 */
	@RequestMapping(value = "/updateMenu.do", method = RequestMethod.POST)
	public void updateMenu(HttpServletRequest request,HttpSession session,PrintWriter pw,Menu menu){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			Menu beforeUpdate = null;
			if(ConstantUtil.WRITELOG){
				beforeUpdate = sysOptLogService.getMenuByMenuId(menu.getMenuId());
			}
			menuService.updateMenu(menu);
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.updateMenu(IpAddressUtil.getReqIp(request),userId,beforeUpdate,menu);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.print("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.print("fail");
		}
	}
}
