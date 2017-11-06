package usi.sys.job.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import usi.sys.dto.PageObj;
import usi.sys.job.dao.SysJobLogDao;
import usi.sys.job.dto.SysJobLogParam;
import usi.sys.job.entity.SysJobLog;

@Service
public class SysJobLogService {
	
	@Resource
	private SysJobLogDao jobLogDao;
	
	/**
	 * 查询所有的job
	 * @return
	 */
	public Map<String,Object> getPagedJob(PageObj pageObj,SysJobLogParam jobLogParam){
		Map<String,Object> map = new HashMap<String, Object>();
		List<SysJobLog> list = jobLogDao.getPagedJobLog(pageObj, jobLogParam);
		map.put("total", pageObj.getTotal());
		map.put("rows", list);
		return map;
	}
	
	@Async
	public void insertJob(SysJobLog jobLog){
		jobLogDao.insertJobLog(jobLog);
	}
}
