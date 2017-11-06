package usi.sys.notice.dao;

import usi.sys.entity.VisitInfo;

public interface VisitInfoDao {
	
	/**
	 * 增加阅读次数
	 * @param visitInfo
	 * @return
	 */
	public int updateVisitNum(long noticeId, long staffId);
	
	/**
	 * 新增记录
	 * @param visitInfo
	 * @return
	 */
	public int insertVisitInfo(VisitInfo visitInfo);
}
