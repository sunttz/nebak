package usi.sys.job.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.DictDto;
import usi.sys.dto.PageObj;
import usi.sys.job.entity.SysJob;
import usi.sys.job.service.QuartzService;
import usi.sys.job.service.SysJobService;
import usi.sys.service.BusiDictService;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;
import usi.sys.util.SysSpringBeanUtil;

@Controller
@RequestMapping("/sysTask")
public class SysJobController {
	
	@Resource
	private SysJobService sysJobService;
	@Resource
	private QuartzService quartzService;
	@Resource
	private BusiDictService busiDictService;
	
	/**
	 * 配置定时任务页面
	 * @return
	 */
	@RequestMapping(value = "/getMain.do", method = RequestMethod.GET)
	public String getMain(HttpServletRequest request,Model model){
		String serverips = CommonUtil.getAllLocalHostIP();
		List<String> beans = SysSpringBeanUtil.getSpringBeanByInterface("ITask");
		List<DictDto> jobGroupList = busiDictService.getDictByCode("JOB_GROUP");
		model.addAttribute("serverips", serverips);
		model.addAttribute("beans", beans);
		model.addAttribute("jobGroupList", jobGroupList);
		return "system/sysTask/taskCfg";
	}
	
	/**
	 * 修改或新增job
	 * @param session
	 * @param job
	 * @return
	 */
	@RequestMapping(value = "/addOrDelJob.do", method = RequestMethod.POST)
	public void addOrDelJob(HttpSession session, PrintWriter writer, SysJob job){
		try {
			if(job.getJobId() != null){
				sysJobService.updateJob(job);
			} else {
				AuthInfo info = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
				job.setStaffId(info.getStaffId());
				sysJobService.insertJob(job);
			}
			writer.write("succ");
		} catch (Exception e) {
			e.printStackTrace();
			writer.write("fail");
		}
	}
	
	/**
	 * @Description 删除任务
	 * @param session
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "/deleteJob.do", method = RequestMethod.POST)
	@ResponseBody
	public String deleteJob(HttpSession session,long jobId){
		try {
			sysJobService.deleteJob(jobId);
			return "succ";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 查询所有的job
	 * @return
	 */
	@RequestMapping(value = "/getPagedJob.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getPagedJob(PageObj getAllJob){
		return sysJobService.getPagedJob(getAllJob);
	}
	
	/**
	 * 启动job
	 * @param session
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value = "/startJob.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> startJob(HttpSession session,SysJob job){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = quartzService.startTask(job);
		} catch (Exception e) {
			e.printStackTrace();
			Boolean flag = false;
			String failCause = "发生异常，启动失败！";
			result.put("flag", flag);
			result.put("failCause", failCause);
		}
		return result;
	}
	
	/**
	 * 停止job
	 * @param session
	 * @param jobInfo
	 * @return
	 */
	@RequestMapping(value = "/stopJob.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> stopJob(HttpSession session,SysJob job){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = quartzService.stopTask(job);
		} catch (Exception e) {
			e.printStackTrace();
			Boolean flag = false;
			String failCause = "发生异常，关闭失败！";
			result.put("flag", flag);
			result.put("failCause", failCause);
		}
		return result;
	}
	
}
