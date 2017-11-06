package usi.sys.job.dao.impl4oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.PageObj;
import usi.sys.job.dao.SysJobDao;
import usi.sys.job.entity.SysJob;

@OracleDb
@Repository
public class SysJobDaoImpl extends JdbcDaoSupport4oracle implements SysJobDao {

	/**
	 * 新增job
	 * @param job
	 */
	public void insertJob(final SysJob job) {
		String sql = " insert into sys_job " +
					"  (job_id, " + 
					"   job_name, " + 
					"	job_group,"+
					"   job_bean_name, " + 
					"   cron_expr, " + 
					"   curr_state, " + 
					"   is_auto_start, " + 
					"   ip_addr, " + 
					"   is_del, " + 
					"   job_memo, " + 
					"   staff_id, " + 
					"	in_param, "+
					"   create_time) " + 
					" values (sys_job_seq.nextval, ?, ?, ?, ?, 0, ?, ?, 0, ?, ?, ?, sysdate) ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, job.getJobName());
				ps.setString(2, job.getJobGroup());
				ps.setString(3, job.getJobBeanName());
				ps.setString(4, job.getCronExpr());
				ps.setInt(5, job.getIsAutoStart());
				ps.setString(6, job.getIpAddr());
				ps.setString(7,job.getJobMemo());
				ps.setLong(8, job.getStaffId());
				ps.setString(9, job.getInParam() == null ? "" : job.getInParam());
			}
		});

	}
	
	/**
	 * 修改job
	 * @param job
	 */
	public void updateJob(final SysJob job){
		String sql = " update sys_job t " +
					"   set t.job_name      = ?, " + 
					"		t.job_group		= ?, "+
					"       t.job_bean_name = ?, " + 
					"       t.cron_expr     = ?, " + 
					"       t.is_auto_start = ?, " + 
					"       t.ip_addr       = ?, " + 
					"       t.job_memo      = ?, " + 
					"       t.in_param      = ?, " + 
					"       t.last_mod_time = sysdate "+
					"   where t.job_id=? ";
		this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, job.getJobName());
				ps.setString(2, job.getJobGroup());
				ps.setString(3, job.getJobBeanName());
				ps.setString(4, job.getCronExpr());
				ps.setInt(5, job.getIsAutoStart());
				ps.setString(6, job.getIpAddr());
				ps.setString(7,job.getJobMemo());
				ps.setString(8,job.getInParam() == null ? "" : job.getInParam());
				ps.setLong(9,job.getJobId());
			}
		});
	}
	
	/**
	 * 更新job状态,是否已启动
	 * @param jobId
	 * @param currState
	 */
	public void updateCurrentState(long jobId,int currState){
		String sql =" update sys_job t set t.curr_state=? ,t.last_mod_time=sysdate where t.job_id=? ";
		this.getJdbcTemplate().update(sql, currState, jobId);
	}
	
	/**
	 * 删除job
	 * @param jobId
	 */
	public void deleteJob(long jobId){
		String sql = " update sys_job t set t.is_del=1, t.last_mod_time=sysdate where t.job_id=? ";
		this.getJdbcTemplate().update(sql, jobId);
	}

	/**
	 * 获取所有的随系统启动的job
	 * @return
	 */
	public List<SysJob> getAutoStartJob(){
		String sql = " select t.job_id, " +
					"      t.job_name, " + 
					"      t.job_group, " + 
					"      t.job_bean_name, " + 
					"      t.cron_expr, " + 
					"      t.curr_state, " + 
					"      t.is_auto_start, " + 
					"      t.ip_addr, " + 
					"      to_char(t.create_time,'yyyy-mm-dd hh24:mi:ss'),"+
					" 	   in_param " + 
					" from sys_job t " + 
					" where t.is_del = 0 " + 
					" and t.is_auto_start=1 ";
		
		return this.getJdbcTemplate().query(sql, new RowMapper<SysJob>(){

			@Override
			public SysJob mapRow(ResultSet rs, int rowNum) throws SQLException {
				SysJob item = new SysJob();
				item.setJobId(rs.getLong(1));
				item.setJobName(rs.getString(2));
				item.setJobGroup(rs.getString(3));
				item.setJobBeanName(rs.getString(4));
				item.setCronExpr(rs.getString(5));
				item.setCurrState(rs.getInt(6));
				item.setIsAutoStart(rs.getInt(7));
				item.setIpAddr(rs.getString(8));
				item.setCreateTime(rs.getString(9));
				item.setInParam(rs.getString(10) == null ? "" : rs.getString(10));
				return item;
			}});
	}
	
	/**
	 * 获取所有的job
	 * @return
	 */
	public List<SysJob> getPagedJob(PageObj pageObj){
		String sql = " select t.job_id, " +
					"       t.job_name, " + 
					"       t.job_group, " + 
					"       t.job_bean_name, " + 
					"       t.cron_expr, " + 
					"       t.curr_state, " + 
					"       t.is_auto_start, " + 
					"       t.ip_addr, " + 
					"       to_char(t.create_time,'yyyy-mm-dd hh24:mi:ss')," +
					"   	s.operator_name," +
					" 		in_param " +
					"  from sys_job t,sys_staff s  " + 
					" where t.is_del = 0 and t.staff_id=s.staff_id order by t.job_group";
		return this.queryByPage(sql,  new RowMapper<SysJob>(){

			@Override
			public SysJob mapRow(ResultSet rs, int rowNum) throws SQLException {
				SysJob item = new SysJob();
				item.setJobId(rs.getLong(1));
				item.setJobName(rs.getString(2));
				item.setJobGroup(rs.getString(3));
				item.setJobBeanName(rs.getString(4));
				item.setCronExpr(rs.getString(5));
				item.setCurrState(rs.getInt(6));
				item.setIsAutoStart(rs.getInt(7));
				item.setIpAddr(rs.getString(8));
				item.setCreateTime(rs.getString(9));
				item.setStaffName(rs.getString(10));
				item.setInParam(rs.getString(11) == null ? "" : rs.getString(11));
				return item;
			}}, pageObj);
	}
	
}
