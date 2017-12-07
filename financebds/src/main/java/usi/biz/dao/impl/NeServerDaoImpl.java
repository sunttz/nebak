package usi.biz.dao.impl;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import usi.biz.dao.NeServerDao;
import usi.biz.entity.AutoLogDto;
import usi.biz.entity.NeServer;
import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.PageObj;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@OracleDb
@Repository
public class NeServerDaoImpl  extends JdbcDaoSupport4oracle implements NeServerDao{

	@Override
	public List<NeServer> getAllOrg() {
		String sql = " select  distinct t.org_id,t.org_name  from ne_server t order by t.org_id asc";
	
	return this.getJdbcTemplate().query(sql, new RowMapper<NeServer>(){
		@Override
		public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
			NeServer record = new NeServer();
			record.setOrgId(rs.getLong(1));
			record.setOrgName(rs.getString(2));
			return record;
		}});
	}

	@Override
	public List<NeServer> getPageAllNE(PageObj pageObj, Long orgId,String deviceType) {
		String sql = "select t.server_id,t.org_id,t.org_name,t.device_name,t.device_type,t.remarks,t.device_addr,t.bak_path,t.user_name,t.pass_word,t.bak_type from ne_server t"
					+" WHERE 1=1";
	    
		if(orgId!=null && !orgId.equals("") && orgId !=-1L){
			sql +=" and t.org_id = "+orgId;
		}
		if(deviceType!=null && !deviceType.equals("-1")){
			sql +=" and t.device_type = '"+deviceType+"'";
		}
		sql+=" order by t.org_id asc";
		return this.queryByPage(sql,  new RowMapper<NeServer>(){
			@Override
			public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
				NeServer record = new NeServer();
				record.setServerId(rs.getLong(1));
				record.setOrgId(rs.getLong(2));
				record.setOrgName(rs.getString(3));
				record.setDeviceName(rs.getString(4));
				record.setDeviceType(rs.getString(5));
				record.setRemarks(rs.getString(6));
				record.setDeviceAddr(rs.getString(7));
				record.setBakPath(rs.getString(8));
				record.setUserName(rs.getString(9));
				record.setPassWord(rs.getString(10));
				record.setBakType(rs.getString(11));
				return record;
			}}, pageObj);
	}

	@Override
	public List<NeServer> getNeServerById(Long serverId) {
		String sql="select t.server_id,t.org_id,t.org_name,t.device_name,t.device_type,t.remarks,t.device_addr,t.bak_path,t.user_name,t.pass_word,t.bak_type from ne_server t"
				+" WHERE t.server_id="+serverId;
		return this.getJdbcTemplate().query(sql, new RowMapper<NeServer>(){
			@Override
			public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
				NeServer record = new NeServer();
				record.setServerId(rs.getLong(1));
				record.setOrgId(rs.getLong(2));
				record.setOrgName(rs.getString(3));
				record.setDeviceName(rs.getString(4));
				record.setDeviceType(rs.getString(5));
				record.setRemarks(rs.getString(6));
				record.setDeviceAddr(rs.getString(7));
				record.setBakPath(rs.getString(8));
				record.setUserName(rs.getString(9));
				record.setPassWord(rs.getString(10));
				record.setBakType(rs.getString(11));
				return record;
			}});
	}

	@Override
	public List<NeServer> getAllNE() {
		// TODO Auto-generated method stub
		String sql = "select t.server_id,t.org_id,t.org_name,t.device_name,t.device_type,t.remarks,t.device_addr,t.bak_path,t.user_name,t.pass_word,t.bak_type from ne_server t"
				+" WHERE 1=1";
		sql+=" order by t.org_id asc";
		return this.getJdbcTemplate().query(sql,  new RowMapper<NeServer>(){
			@Override
			public NeServer mapRow(ResultSet rs, int rowNum) throws SQLException {
				NeServer record = new NeServer();
				record.setServerId(rs.getLong(1));
				record.setOrgId(rs.getLong(2));
				record.setOrgName(rs.getString(3));
				record.setDeviceName(rs.getString(4));
				record.setDeviceType(rs.getString(5));
				record.setRemarks(rs.getString(6));
				record.setDeviceAddr(rs.getString(7));
				record.setBakPath(rs.getString(8));
				record.setUserName(rs.getString(9));
				record.setPassWord(rs.getString(10));
				record.setBakType(rs.getString(11));
				return record;
			}});
	}

	@Override
	public List<AutoLogDto> getAutoResult(String dateTime) {
		// TODO Auto-generated method stub
		String sql = "select a.server_id,"+
				     "a.org_id,"+
				     "a.org_name,"+
				     "a.device_name,"+
				     "a.device_type,"+
				     "a.remarks,"+
				     "a.device_addr,"+
				     "a.bak_path,"+
				     "a.user_name,"+
				     "a.pass_word,"+
				     "b.log_id,"+
				     "b.bak_flag,"+
					 "a.bak_type,"+
				     "to_char(b.create_date, 'yyyy-mm-dd') as create_date"+
				     " from ne_server a, biz_auto_log b"+
				     " where a.server_id = b.server_id";
		if(!dateTime.equals("")){
			sql+=" and to_char(b.create_date, 'yyyymmdd') ='"+ dateTime +"'";
		}
		sql+=" order by a.org_id";
		return this.getJdbcTemplate().query(sql,  new RowMapper<AutoLogDto>(){
			@Override
			public AutoLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				AutoLogDto record = new AutoLogDto();
				record.setServerId(rs.getLong(1));
				record.setOrgId(rs.getLong(2));
				record.setOrgName(rs.getString(3));
				record.setDeviceName(rs.getString(4));
				record.setDeviceType(rs.getString(5));
				record.setRemarks(rs.getString(6));
				record.setDeviceAddr(rs.getString(7));
				record.setBakPath(rs.getString(8));
				record.setUserName(rs.getString(9));
				record.setPassWord(rs.getString(10));
				record.setLogId(rs.getLong(11));
				record.setBakFlag(rs.getInt(12));
				record.setBakType(rs.getString(13));
				record.setCreateDate(rs.getString(14));
				return record;
			}});
	}

	@Override
	public int saveNeServer(final NeServer neServer) {
		String sql = "INSERT INTO NE_SERVER " +
				" (SERVER_ID,ORG_NAME,DEVICE_NAME,DEVICE_TYPE,REMARKS,DEVICE_ADDR,BAK_PATH,USER_NAME,PASS_WORD,ORG_ID,BAK_TYPE)" +
				" VALUES (NE_SERVER_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1,neServer.getOrgName());
				ps.setString(2,neServer.getDeviceName());
				ps.setString(3,neServer.getDeviceType());
				ps.setString(4,neServer.getRemarks());
				ps.setString(5,neServer.getDeviceAddr());
				ps.setString(6,neServer.getBakPath());
				ps.setString(7,neServer.getUserName());
				ps.setString(8,neServer.getPassWord());
				ps.setLong(9,neServer.getOrgId());
				ps.setString(10,neServer.getBakType());
			}
		});
	}

	@Override
	public int updateNeServer(final NeServer neServer) {
		String sql = "UPDATE NE_SERVER SET ORG_NAME=?,DEVICE_NAME=?,DEVICE_TYPE=?,REMARKS=?,DEVICE_ADDR=?,BAK_PATH=?,USER_NAME=?,PASS_WORD=?,ORG_ID=?,BAK_TYPE=? WHERE SERVER_ID=?";
		return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1,neServer.getOrgName());
				ps.setString(2,neServer.getDeviceName());
				ps.setString(3,neServer.getDeviceType());
				ps.setString(4,neServer.getRemarks());
				ps.setString(5,neServer.getDeviceAddr());
				ps.setString(6,neServer.getBakPath());
				ps.setString(7,neServer.getUserName());
				ps.setString(8,neServer.getPassWord());
				ps.setLong(9,neServer.getOrgId());
				ps.setString(10,neServer.getBakType());
				ps.setLong(11,neServer.getServerId());
			}
		});
	}

	@Override
	public int deleteNeServer(final Long serverId) {
		String sql = "DELETE FROM NE_SERVER WHERE SERVER_ID = ?";
		return this.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1,serverId);
			}
		});
	}
}
