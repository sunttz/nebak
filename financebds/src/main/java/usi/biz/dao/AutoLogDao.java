package usi.biz.dao;

import java.util.List;

import usi.biz.entity.AutoLog;

public interface AutoLogDao {
	
	public void saveAutoLog(AutoLog atta);
	
	public List<AutoLog> queryAutoLog(Long logId);
	
	public void deleteAutoLogByTime();
}
