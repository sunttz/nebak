package usi.sys.notice.dao.impl4oracle;

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

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.entity.NoticeReply;
import usi.sys.notice.dao.NoticeReplyDao;

/**
 * 公告回复DAO
 * @author 凡
 *
 */
@OracleDb
@Repository
public class NoticeReplyDaoImpl extends JdbcDaoSupport4oracle implements NoticeReplyDao {
	
	/**
	 * 插入一条公告回复
	 * @return
	 */
	public long insertReply(final NoticeReply reply) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql = " insert into sys_bulletin_comment (comment_id, bulletin_id, staff_id, content, opt_time) "+
							" values (sys_bulletin_comment_seq.nextval, ?, ?, ?, ?)";
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
	
	/**
	 * 查出某个公告的回复
	 * @param noticeId
	 * @return
	 */
	public List<NoticeReply> queryReplysByNoticeId(long noticeId) {
		String sql = "select t1.comment_id,\n" +
						"       t1.content,\n" + 
						"       to_char(t1.opt_time, 'YYYY-MM-DD HH24:MI:SS') opt_time,\n" + 
						"       t2.staff_id,\n" + 
						"       nvl(t2.operator_name, '未知') operator_name,\n" + 
						"       t3.org_name\n" + 
						"  from sys_bulletin_comment t1, sys_staff t2, sys_org t3\n" + 
						" where t1.staff_id = t2.staff_id(+) and t2.org_id = t3.org_id\n" + 
						"   and t1.bulletin_id = ?\n" + 
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
