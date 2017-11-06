package usi.sys.script.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.PageObj;
import usi.sys.script.service.ScriptService;
import usi.sys.script.service.SysScriptsService;
import usi.sys.service.BusiDictService;
import usi.sys.util.JacksonUtil;

@Controller
@RequestMapping("/sys/script")
public class ScriptController {
	
	@Resource
	BusiDictService busiDictService;
	
	@Resource
	ScriptService scriptService;
	
	@Resource
    SysScriptsService sysScriptsService;
	
	/**
	 * 接收通知，重新发布脚本
	 * @param scriptCodes
	 * @throws Exception
	 */
	@RequestMapping(value="/publishScript.notice",method=RequestMethod.POST)
	public void publishScript(String scriptCodes) throws Exception{
		List<String> scriptCodeList = JacksonUtil.json2list(scriptCodes, ArrayList.class, String.class);
		scriptService.refreshScript(scriptCodeList);
	}

	/**
	 * 进入执行脚本配置页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/toMenu.do", method = RequestMethod.GET)
	public String toMainPage(Model model) {
		model.addAttribute("module", busiDictService.getDictByCode("MODULE"));
		model.addAttribute("scriptType", busiDictService.getDictByCode("SCRIPT_TYPES"));
		return "system/scripts/scripts";
	}

	/**
	 * 查询执行脚本列表
	 * 
	 * @param scriptCode 脚本编码
	 * @param scriptName 脚本名称
	 * @param scriptCont 脚本内容
	 * @param module 所属模块
	 * @param scriptType 脚本类型
	 * @param isDeploy 发布状态
	 * @param mockInparam 模拟入参
	 * @param memo 备注
	 * @return
	 */
	@RequestMapping(value = "/getScrConfig.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getScrConfig(String scriptCode, String scriptName, String scriptContent, Integer module, Integer scriptType,
			Integer isDeploy, String mockInparam, String memo, PageObj pageObj) {
		return sysScriptsService.getScrConfig(scriptCode, scriptName, scriptContent, module, scriptType, isDeploy, mockInparam, memo, pageObj);
	}

	/*
	 * 发布执行脚本
	 */
	@RequestMapping(value = "/publishScript.do", method = RequestMethod.POST)
	public void publishScript(HttpServletRequest request, PrintWriter pw, @RequestParam(value = "scriptCodes[]") List<String> scriptCodes) {
		try {
			scriptService.noticeForRefreshScript(scriptCodes);
			pw.write("success");
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("fail");
		}
	}
	
	/*
	 * 发布全部执行脚本
	 */
	@RequestMapping(value = "/publishAllScript.do", method = RequestMethod.POST)
	public void publishAllScript(HttpServletRequest request, PrintWriter pw) {
		try {
			scriptService.noticeForRefreshAllScript();
			pw.write("success");
		} catch (Exception e) {
			e.printStackTrace();
			pw.write("fail");
		}
	}
	
	/*
	 * 接受通知，发布全部脚本
	 */
	@RequestMapping(value = "/publishAllScript.notice", method = RequestMethod.POST)
	public void doPublishAllScript() {
		scriptService.initAllScriptCache();
	}
}
