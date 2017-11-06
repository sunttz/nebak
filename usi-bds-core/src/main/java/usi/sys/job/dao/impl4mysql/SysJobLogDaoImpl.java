package usi.sys.job.dao.impl4mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dto.PageObj;
import usi.sys.job.dao.SysJobLogDao;
import usi.sys.job.dto.SysJobLogParam;
import usi.sys.job.entity.SysJobLog;
import usi.sys.util.CommonUtil;

/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class SysJobLogDaoImpl extends JdbcDaoSupport4mysql implements SysJobLogDao{

	@Override
	public void insertJobLog(final SysJobLog jobLog) {
		String sql = "insert into sys_job_log" 
				+"  (job_id,job_name,ip_addr,job_start_time,job_end_time,is_success_end) " 
				+	"values ( ?, ?, ?, ?, ?,?)";

		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, jobLog.getJobId());
				ps.setString(2, jobLog.getJobName());
				ps.setString(3, jobLog.getIpAddr());
				ps.setTimestamp(4,new Timestamp(jobLog.getJobStartTime().getTime()));
				ps.setTimestamp(5,new Timestamp(jobLog.getJobEndTime().getTime()));
				ps.setInt(6, jobLog.getIsSuccessEnd());
			}
		});
		
	}

	@Override
	public List<SysJobLog> getPagedJobLog(PageObj pageObj, SysJobLogParam logParam) {
		String sql = " select t.job_log_id,t.job_id,t.job_name,t.ip_addr,"
				+ "t.job_start_time,t.job_end_time,t.is_success_end " +
				" from sys_job_log t where 1=1 ";
		if(logParam != null){
			if(CommonUtil.hasValue(logParam.getJobName())){
				sql += " and t.job_name like '%" + logParam.getJobName() + "%' ";
			}
			if(CommonUtil.hasValue(logParam.getStartTime())){
				sql += " and t.job_start_time >= str_to_date('"+logParam.getStartTime()+"','%Y-%m-%d %H:%i:%s')  ";
			}
			if(CommonUtil.hasValue(logParam.getEndTime())){
				sql += " and t.job_start_time <= str_to_date('"+logParam.getEndTime()+"','%Y-%m-%d %H:%i:%s')  ";
			}
		}
		sql += " order by job_start_time desc";
		return this.queryByPage(sql,  new RowMapper<SysJobLog>(){

			@Override
			public SysJobLog mapRow(ResultSet rs, int rowNum) throws SQLException {
				SysJobLog item = new SysJobLog();
				item.setJobLogId(rs.getLong("job_log_id"));
				item.setJobId(rs.getLong("job_id"));
				item.setJobName(rs.getString("job_name"));
				item.setIpAddr(rs.getString("ip_addr"));
				item.setJobStartTime(new Date(rs.getTimestamp("job_start_time").getTime()));
				item.setJobEndTime(new Date(rs.getTimestamp("job_end_time").getTime()));
				item.setIsSuccessEnd(rs.getInt("is_success_end"));
				return item;
			}}, pageObj);
	}

}
