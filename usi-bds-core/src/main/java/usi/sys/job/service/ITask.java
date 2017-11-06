package usi.sys.job.service;

import usi.sys.job.entity.SysJob;

/**
 * 需要定时执行类实现此接口，且类要注解为bean
 * @author lmwang
 * 创建时间：2014-7-2 上午10:22:42
 */
public interface ITask {
	
	/**
	 * 实现此方法，做具体的业务
	 * @param SysJob 方法入参，包含任务信息
	 */
	public void doJob(SysJob sysJob);

}
