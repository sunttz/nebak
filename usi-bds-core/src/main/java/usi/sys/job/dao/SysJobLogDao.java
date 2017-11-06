package usi.sys.job.dao;

import java.util.List;

import usi.sys.dto.PageObj;
import usi.sys.job.dto.SysJobLogParam;
import usi.sys.job.entity.SysJobLog;

public interface SysJobLogDao {

	/**
	 * 新增job
	 * @param job
	 */
	public void insertJobLog(final SysJobLog jobLog);
	
	/**
	 * 获取所有的job
	 * @return
	 */
	public List<SysJobLog> getPagedJobLog(PageObj pageObj,SysJobLogParam logParam);
	
}
