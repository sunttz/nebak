package usi.sys.job.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dto.PageObj;
import usi.sys.job.dao.SysJobDao;
import usi.sys.job.entity.SysJob;

@Service
public class SysJobService {
	
	@Resource
	private SysJobDao sysJobDao;
	
	/**
	 * 查询所有的job
	 * @return
	 */
	public Map<String,Object> getPagedJob(PageObj pageObj){
		Map<String,Object> map = new HashMap<String, Object>();
		List<SysJob> list = sysJobDao.getPagedJob(pageObj);
		map.put("total", pageObj.getTotal());
		map.put("rows", list);
		return map;
	}
	
	/**
	 * 查询所有随系统启动的job
	 * @return
	 */
	public List<SysJob> getAutoStartJob(){
		List<SysJob> list = sysJobDao.getAutoStartJob();
		return list;
	}
	
	/**
	 * 新增job
	 * @param job
	 */
	@Transactional(rollbackFor=Exception.class)
	public void insertJob(SysJob job){
		sysJobDao.insertJob(job);
	}
	
	/**
	 * 修改job
	 * @param job
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateJob(SysJob job){
		sysJobDao.updateJob(job);
	}
	
	/**
	 * 删除job
	 * @param jobId
	 */
	@Transactional(rollbackFor=Exception.class)
	public void deleteJob(long jobId){
		sysJobDao.deleteJob(jobId);
	}
	/**
	 * 更新job状态
	 * @param jobId
	 * @param currState
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateCurrentState(long jobId,int currState){
		sysJobDao.updateCurrentState(jobId, currState);
	}
}
