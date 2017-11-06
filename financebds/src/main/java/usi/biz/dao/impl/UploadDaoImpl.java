package usi.biz.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.biz.dao.UploadDao;
import usi.biz.entity.Attachment;
import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.PageObj;

@OracleDb
@Repository
public class UploadDaoImpl extends JdbcDaoSupport4oracle implements UploadDao{

	@Override
	public long saveUpload(final Attachment atta) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql ="INSERT INTO BIZ_ATTACHMENT (ATTACHMENT_ID,REAL_NAME,SAVE_NAME,CREATE_TIME)"
				+ " VALUES(BIZ_ATTACHMENT_SEQ.nextval,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,new String[]{"ATTACHMENT_ID"});
					ps.setString(1, atta.getRealName());
					ps.setString(2, atta.getSaveName());
					ps.setTimestamp(3,new java.sql.Timestamp(atta.getCreateTime().getTime()));
					return ps;
				}
			}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public List<Attachment> downloadList(PageObj pageObj,String realName,String startTime,String endTime){
		String sql = "SELECT a.ATTACHMENT_ID,a.REAL_NAME,a.SAVE_NAME,a.CREATE_TIME FROM BIZ_ATTACHMENT a "
				+"WHERE 1=1";
	    
		if(realName!=null && !realName.equals("")){
			sql +=" and a.real_name like '%"+realName+"%'";
		}else{
			sql +=" and a.real_name like '%'";
		}
		if(startTime !=null && !startTime.equals("")){
			sql +=" and a.create_time>= to_timestamp('"+startTime+"','yyyy-mm-dd hh24:mi:ss.FF') ";
		}
		if(endTime !=null && !endTime.equals("")){
			sql +=" and a.create_time<= to_timestamp('"+endTime+" 23:59:59"+"','yyyy-mm-dd hh24:mi:ss.FF') ";
		}
		sql+=" order by a.create_time desc";
		return this.queryByPage(sql,  new RowMapper<Attachment>(){
			@Override
			public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
				Attachment attachment = new Attachment();
				attachment.setAttachmentId(rs.getLong(1));
				attachment.setRealName(rs.getString(2));
				attachment.setSaveName(rs.getString(3));
				attachment.setCreateTime(new Date(rs.getTimestamp(4).getTime()));
				return attachment;
			}}, pageObj);
	}
}
