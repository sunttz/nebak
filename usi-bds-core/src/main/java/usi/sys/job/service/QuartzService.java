package usi.sys.job.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import usi.sys.job.entity.SysJob;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConfigUtil;

/**
 * 系统启动时候自动执行这个bean的方法，初始化需要随工程启动而启动的定时任务
 * @author lmwang
 * 创建时间：2014-7-2 上午10:54:15
 */
@Service
public class QuartzService implements ServletContextAware {
	
	private static final Log LOG = LogFactory.getLog(QuartzService.class);
	
	@Autowired
	private SysJobService sysJobService;
	
	/**
	 * servlet上下文
	 */
	private ServletContext servletContext;
	
	/**
	 * 实现ServletContextAware接口的方法，注入ServletContext
	 * @param servletContext
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext =servletContext;
	}
	
	/**
	 * bean初始化好之后执行（初始化需要随工程启动而启动的定时任务）
	 */
	@PostConstruct
	public void  init() {
		if("true".equals(ConfigUtil.getValue("useBdsScheduler"))){
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = null;
			//本机所有ip地址（多网卡，ip用逗号分割）
			String ips = CommonUtil.getAllLocalHostIP();
			
			try {
				//设置全局的scheduler
				scheduler = schedulerFactory.getScheduler();
				servletContext.setAttribute("scheduler", scheduler);
				//启动需要随工程启动而启动的定时任务
				List<SysJob> jobList = sysJobService.getAutoStartJob();
				if(null!=jobList) {
					for(SysJob sysJob: jobList) {
						//比对配置的ip是否在本机ip串中，是的话，在本机启动此任务
						if(ips.indexOf(sysJob.getIpAddr()) != -1) {
							startTask(sysJob);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * bean销毁之前（停止所有启动了的定时任务）
	 */
	@PreDestroy
	public void destroy() {
		if("true".equals(ConfigUtil.getValue("useBdsScheduler"))){
			Scheduler scheduler = (Scheduler) servletContext.getAttribute("scheduler");
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 启动任务
	 * @param sysJob 定时任务对象
	 * @return
	 */
	public Map<String, Object> startTask(SysJob sysJob) {
		
		LOG.info("在IP:"+sysJob.getIpAddr()+"：启动任务："+sysJob.getJobName());
		Scheduler scheduler = (Scheduler) servletContext.getAttribute("scheduler");
		
		Map<String, Object> map = new HashMap<String, Object>();
		Boolean flag = null;
		String failCause = "";
		
		String jobName = sysJob.getJobId() + "_timer_job";
		String groupName = sysJob.getJobGroup();
		String triggerName = sysJob.getJobId() + "_timer_trigger";
		
		JobDetail job = JobBuilder.newJob(TimerJob.class).withIdentity(jobName, groupName).build();
		job.getJobDataMap().put("sysJob", sysJob);
		CronTrigger trigger = null;
		
		try {
			//利用框架自身的异常验证cron表达式
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, groupName) 
					.withSchedule(CronScheduleBuilder.cronSchedule(sysJob.getCronExpr())).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("flag", false);
			map.put("failCause", "非法的cron表达式！");
			return map;
		}
		
		try {
			if(scheduler.checkExists(JobKey.jobKey(jobName, groupName))) {
				map.put("flag", false);
				map.put("failCause", "已经被启动！");
				//防止数据库中任务状态与内存中不一致
				sysJobService.updateCurrentState(sysJob.getJobId(), 1);
				return map;
			}
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			flag = true;
			//0表示定时任务的当前状态为停止，1表示定时任务的当前状态是运行
			sysJobService.updateCurrentState(sysJob.getJobId(), 1);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			failCause = "未知异常，启动失败！";
		}
		
		map.put("flag", flag);
		map.put("failCause", failCause);
		return map;
	}
	
	/**
	 * 停止任务
	 * @param sysJob 定时任务对象
	 * @return
	 */
	public Map<String, Object> stopTask(SysJob sysJob) {
		
		LOG.info("在IP:"+sysJob.getIpAddr()+"：停止任务："+sysJob.getJobName());
		Scheduler scheduler = (Scheduler) servletContext.getAttribute("scheduler");
		
		Map<String, Object> map = new HashMap<String, Object>();
		Boolean flag = null;
		String failCause = "";
		
		String jobName = sysJob.getJobId() + "_timer_job";
		String groupName = sysJob.getJobGroup();
		String triggerName = sysJob.getJobId() + "_timer_trigger";	
		
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,groupName);
		JobKey jobKey = JobKey.jobKey(jobName, groupName);
		
		try {
			if(!scheduler.checkExists(jobKey)) {
				map.put("flag", false);
				map.put("failCause", "已经被关闭！");
				//防止数据库中任务状态与内存中不一致
				sysJobService.updateCurrentState(sysJob.getJobId(), 0);
				return map;
			}
			
			scheduler.pauseTrigger(triggerKey);// 停止触发器
			scheduler.unscheduleJob(triggerKey);// 移除触发器
			scheduler.deleteJob(jobKey);// 删除任务
			flag = true;
			//0表示定时任务的当前状态为停止，1表示定时任务的当前状态是运行
			sysJobService.updateCurrentState(sysJob.getJobId(), 0);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			failCause = "未知异常，关闭失败！";
		}
		
		map.put("flag", flag);
		map.put("failCause", failCause);
		return map;
	}
	
}
