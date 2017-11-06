package usi.sys.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.service.StaffService;
import usi.sys.service.SysOptLogService;
import usi.sys.util.ConfigUtil;
import usi.sys.util.ConstantUtil;
import usi.sys.util.IpAddressUtil;

/**
 * 登录 退出系统
 * @author lmwang
 * 创建时间：2014-3-26 下午2:37:04
 */
@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Resource
	private StaffService staffService;
	
	@Resource
	private SysOptLogService sysOptLogService;
	
	/**
	 * 登录验证
	 * @param authInfo 登录提交的信息映射的实体
	 * @param request
	 * @param response
	 * @return
	 * 2014-12-09 lmwang 解决appscan扫描出的会话标识未更新（登录前让旧的session失效，登录成功后新生成session）和设置cookie的httponly（登录成功后）
	 */
	@RequestMapping(value = "/doLogin.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doLogin(AuthInfo authInfo,HttpServletRequest request,HttpServletResponse response) {
		if(ConstantUtil.WRITELOG){
			try {
				sysOptLogService.doLogin(IpAddressUtil.getReqIp(request),authInfo.getUserId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//让旧的session失效
		HttpSession sessionOld = request.getSession(false);
		if(sessionOld != null){
			sessionOld.invalidate();
		}
		
		//验证登录
		Map<String, Object> msgMap = staffService.validateLogin(authInfo);
		
		//重新生成session（应appscan要求，修改会话标识未更新）
		HttpSession session = request.getSession(true);
		
		if((Boolean) msgMap.get("msgFlag")) {
			//叶子菜单
			Map<String, String> leafMenusMap = staffService.getLeafAuthMenusByUserId(authInfo.getUserId());
			session.setAttribute(ConstantUtil.AUTH_INFO, authInfo);
			session.setAttribute(ConfigUtil.getValue("menuKey"), leafMenusMap);
			
			//设置httponly（应appscan要求）
			String sessionId = request.getSession().getId();
			String contextPath = request.getContextPath();
			String cookieValue = "JSESSIONID=" + sessionId + ";Path=" +contextPath+ "/;HTTPOnly";
			response.setHeader("Set-Cookie", cookieValue);
		}
		return msgMap;
	}
	
	/**
	 * 退出系统
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
	public String logout(HttpSession session){
		session.invalidate();
		return "redirect:/";
	}
}
