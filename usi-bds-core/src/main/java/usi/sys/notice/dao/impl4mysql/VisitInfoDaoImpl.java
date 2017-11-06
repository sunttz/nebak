package usi.sys.notice.dao.impl4mysql;

import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.entity.VisitInfo;
import usi.sys.notice.dao.VisitInfoDao;

/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class VisitInfoDaoImpl extends JdbcDaoSupport4mysql implements
		VisitInfoDao {

	@Override
	public int updateVisitNum(long noticeId, long staffId) {
		String sql = "update sys_bulletin_read set read_cnt = read_cnt+1 "
				+ "where bulletin_id = ? and reader = ?";
		return this.getJdbcTemplate().update(sql, noticeId, staffId);
	}

	@Override
	public int insertVisitInfo(VisitInfo visitInfo) {
		String sql = " insert into sys_bulletin_read (bulletin_id, reader, read_cnt) "
				+ " values (?, ?, 1)";
		return this.getJdbcTemplate().update(sql, visitInfo.getNoticeId(),
				visitInfo.getStaffId());
	}

}
