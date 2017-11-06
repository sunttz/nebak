package usi.sys.notice.dao.impl4oracle;

import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.entity.VisitInfo;
import usi.sys.notice.dao.VisitInfoDao;

@OracleDb
@Repository
public class VisitInfoDaoImpl extends JdbcDaoSupport4oracle implements VisitInfoDao {
	
	/**
	 * 增加阅读次数
	 * @param visitInfo
	 * @return
	 */
	public int updateVisitNum(long noticeId, long staffId) {
		
		String sql = "update sys_bulletin_read t set t.read_cnt = t.read_cnt+1 where t.bulletin_id = ? and t.reader = ?";
		return this.getJdbcTemplate().update(sql, noticeId, staffId);
	}
	
	/**
	 * 新增记录
	 * @param visitInfo
	 * @return
	 */
	public int insertVisitInfo(VisitInfo visitInfo) {
		String sql = " insert into sys_bulletin_read (read_id, bulletin_id, reader, read_cnt) "+
				" values (sys_bulletin_read_seq.nextval, ?, ?, 1)";
		return this.getJdbcTemplate().update(sql, visitInfo.getNoticeId(), visitInfo.getStaffId());
	}
}
