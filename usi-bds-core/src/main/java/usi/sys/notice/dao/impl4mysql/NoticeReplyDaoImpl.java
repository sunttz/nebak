package usi.sys.notice.dao.impl4mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.entity.NoticeReply;
import usi.sys.notice.dao.NoticeReplyDao;

/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class NoticeReplyDaoImpl extends JdbcDaoSupport4mysql implements NoticeReplyDao{

	@Override
	public long insertReply(final NoticeReply reply) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql = " insert into sys_bulletin_comment ( bulletin_id, staff_id, content, opt_time) "+
							" values (?, ?, ?, ?)";
				PreparedStatement ps = con.prepareStatement(sql,new String[]{"comment_id"});
				ps.setLong(1, reply.getNoticeId());
				ps.setLong(2, reply.getStaffId());
				ps.setString(3, reply.getReplyContent());
				ps.setTimestamp(4, new Timestamp(reply.getPublishDate().getTime()));
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public List<NoticeReply> queryReplysByNoticeId(long noticeId) {
		String sql = "select t1.comment_id," +
				"       t1.content," + 
				"       date_format(t1.opt_time, '%y-%m-%d %h:%i:%s') opt_time," + 
				"       t2.staff_id," + 
				"       ifnull(t2.operator_name,'未知') operator_name, " + 
				"       t3.org_name " + 
				"  from sys_bulletin_comment t1 left join sys_staff t2 on t1.staff_id = t2.staff_id "
				+ "inner join sys_org t3 on t2.org_id = t3.org_id" + 
				"   where t1.bulletin_id = ?"+  
				"   order by t1.opt_time";

return this.getJdbcTemplate().query(sql, new RowMapper<NoticeReply>() {

	@Override
	public NoticeReply mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		NoticeReply reply = new NoticeReply();
		reply.setReplyId(rs.getLong("comment_id"));
		reply.setReplyContent(rs.getString("content"));
		reply.setPublishTime(rs.getString("opt_time"));
		reply.setStaffId(rs.getLong("staff_id"));
		reply.setUserName(rs.getString("operator_name"));
		reply.setOrgName(rs.getString("org_name"));
		return reply;
	}
}, noticeId);
	}

}
