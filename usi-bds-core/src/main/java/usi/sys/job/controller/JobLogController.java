package usi.sys.job.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.PageObj;
import usi.sys.job.dto.SysJobLogParam;
import usi.sys.job.service.SysJobLogService;

@Controller
@RequestMapping("/sysJobLog")
public class JobLogController {
	
	@Resource
	private SysJobLogService jobLogService;
	
	/**
	 * 任务日志页面
	 * @return
	 */
	@RequestMapping(value = "/getMain.do", method = RequestMethod.GET)
	public String getMain(Model model){
		return "system/sysTask/taskLog";
	}
	
	/**
	 * 查询所有的job
	 * @return
	 */
	@RequestMapping(value = "/getPagedLog.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getPagedLog(PageObj pageObj,SysJobLogParam jobLogParam){
		return jobLogService.getPagedJob(pageObj,jobLogParam);
	}
	
}
