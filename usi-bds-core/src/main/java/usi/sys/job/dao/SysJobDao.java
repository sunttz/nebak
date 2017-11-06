package usi.sys.job.dao;

import java.util.List;

import usi.sys.dto.PageObj;
import usi.sys.job.entity.SysJob;

public interface SysJobDao {

	/**
	 * 新增job
	 * @param job
	 */
	public void insertJob(final SysJob job);
	
	/**
	 * 修改job
	 * @param job
	 */
	public void updateJob(final SysJob job);
	
	/**
	 * 更新job状态,是否已启动
	 * @param jobId
	 * @param currState
	 */
	public void updateCurrentState(long jobId,int currState);
	
	/**
	 * 删除job
	 * @param jobId
	 */
	public void deleteJob(long jobId);

	/**
	 * 获取所有的随系统启动的job
	 * @return
	 */
	public List<SysJob> getAutoStartJob();
	
	/**
	 * 获取所有的job
	 * @return
	 */
	public List<SysJob> getPagedJob(PageObj pageObj);
	
}
