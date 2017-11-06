package usi.sys.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.AuthMenu;
import usi.sys.service.StaffService;
import usi.sys.util.ConfigUtil;
import usi.sys.util.ConstantUtil;
import usi.sys.util.JacksonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 控制web项目的根目录跳转
 * @author fan.fan
 * @date 2014-3-27 下午1:59:58
 */
@Controller
public class HomeController {

	@Resource
	private StaffService staffService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(HttpSession session) {
		AuthInfo authInfo = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
		return authInfo==null?"system/login":"redirect:/index.do";
	}
	
	@RequestMapping(value = "/index.do", method = RequestMethod.GET)
	public String main(HttpSession session,Model model) {
		AuthInfo authInfo = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
		//菜单
		String menus = "";
		List<AuthMenu> menuList = staffService.getAuthMenusByUserId(authInfo.getUserId());
		try {
			menus = JacksonUtil.obj2json(menuList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		model.addAttribute("menus", menuList);
		model.addAttribute("menusStr", menus);
		
		//获取默认的一级菜单个数
		model.addAttribute("lv1MenuNum", ConfigUtil.getValue("lv1MenuNum"));
		return "system/index";
	}
	
	/**
	 * 修改密码
	 * @param session
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	@RequestMapping(value = "/updatePwd.do",method =RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updatePwd(HttpSession session, String oldPwd, String newPwd) {
		AuthInfo authInfo = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
		Map<String, Object> msgMap = staffService.updatePwd(authInfo.getStaffId(), authInfo.getPassword(), oldPwd, newPwd);
		if((Boolean) msgMap.get("msgFlag")) {
			//修改成功，更新session的密码信息
			authInfo.setPassword(newPwd);
		}
		return msgMap;
	}
}
