package usi.sys.job.service;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import usi.sys.job.entity.SysJob;
import usi.sys.job.entity.SysJobLog;
import usi.sys.util.GlobalApplicationContextHolder;

/**
 * 实现job接口，实现execute方法
 * @author lmwang
 * 创建时间：2014-7-2 下午3:43:25
 */
public class TimerJob implements Job{
	
	/**
	 * 从JobDataMap中取出bean的名称，根据接口调用方法
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		SysJobLogService sysJobLogService = (SysJobLogService) GlobalApplicationContextHolder.getBean("sysJobLogService");
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		SysJob sysJob = (SysJob) dataMap.get("sysJob");
		//设置任务日志
		SysJobLog sysJobLog = new SysJobLog();
		sysJobLog.setJobId(sysJob.getJobId());
		sysJobLog.setJobName(sysJob.getJobName());
		sysJobLog.setIpAddr(sysJob.getIpAddr());
		sysJobLog.setJobStartTime(new Date());
		//默认正常结束
		sysJobLog.setIsSuccessEnd(1);
		
		try {
			ITask iTask = (ITask) GlobalApplicationContextHolder.getBean(sysJob.getJobBeanName());
			iTask.doJob(sysJob);
			//设置结束时间
			sysJobLog.setJobEndTime(new Date());
			//保存定时任务日志
			sysJobLogService.insertJob(sysJobLog);
		} catch (Exception e) {
			//设置结束时间
			sysJobLog.setJobEndTime(new Date());
			//设置异常结束
			sysJobLog.setIsSuccessEnd(0);
			//保存定时任务日志
			sysJobLogService.insertJob(sysJobLog);
			e.printStackTrace();
			//job运行异常就停止
			throw new JobExecutionException();
		}
		
	}
	
}
