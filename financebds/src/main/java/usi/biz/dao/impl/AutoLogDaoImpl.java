package usi.biz.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.biz.dao.AutoLogDao;
import usi.biz.entity.AutoLog;
import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
@OracleDb
@Repository
public class AutoLogDaoImpl extends JdbcDaoSupport4oracle implements AutoLogDao{

	/**
	 * 保存记录
	 * @return
	 */
	@Override
	public void saveAutoLog(final AutoLog atta) {
		// TODO Auto-generated method stub
		String sql = " insert into biz_auto_log " +
				"  (log_id, " + 
				"   server_id, " + 
				"	bak_flag,"+
				"   create_date) " + 
				" values (BIZ_AUTO_LOG_SEQ.nextval, ?, ?, sysdate) ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, atta.getServerId());
				ps.setInt(2, atta.getBakFlag());
			}
		});
	}
	/**
	 * 查询记录
	 * @return
	 */
	@Override
	public List<AutoLog> queryAutoLog(Long logId) {
		// TODO Auto-generated method stub
		String sql = " select t.log_id, " +
				"      t.server_id, " + 
				"      t.bak_flag, " + 
				"      to_char(t.create_date,'yyyy-mm-dd hh24:mi:ss'),"+
				" from biz_auto_log t " + 
				" where t.log_id =" +logId;
	
	return this.getJdbcTemplate().query(sql, new RowMapper<AutoLog>(){
		@Override
		public AutoLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			AutoLog item = new AutoLog();
			item.setLogId(rs.getLong(1));
			item.setServerId(rs.getLong(2));
			item.setBakFlag(rs.getInt(3));
			item.setCreateDate(rs.getDate(4));
			return item;
		}});
	}
	/**
	 * 通过时间删除记录
	 * @return
	 */
	@Override
	public void deleteAutoLogByTime() {
		// TODO Auto-generated method stub
		Calendar now = Calendar.getInstance();  
		String year=String.valueOf(now.get(Calendar.YEAR));
		String month= String.valueOf(now.get(Calendar.MONTH) + 1);
		String day=String.valueOf(now.get(Calendar.DAY_OF_MONTH));
		month=month.length()<2?'0'+month:month;
		day=day.length()<2?'0'+day:day;
		String createDate=year+month+day;
		String sql = " delete from biz_auto_log t where to_char(t.create_date,'yyyymmdd')= '"+createDate+"'";
		this.getJdbcTemplate().update(sql);
	}

}
