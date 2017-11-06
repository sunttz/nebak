package usi.sys.notice.dao.impl4oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.common.annotation.OracleDb;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.dto.NoticeItem;
import usi.sys.entity.Notice;
import usi.sys.notice.dao.NoticeDao;
import usi.sys.util.CommonUtil;

@OracleDb
@Repository
public class NoticeDaoImpl extends JdbcDaoSupport4oracle implements NoticeDao {

	/**
	 * 插入公告
	 * @param notice
	 * @return 主键
	 */
	public long insertNotice(final Notice notice) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql = " insert into sys_bulletin (bulletin_id, title, content, state, read_cnt, create_staff, create_time, lose_time, release_time, reply_flag) "+
							" values (sys_bulletin_seq.nextval, ?, ?, ?, 0, ?, sysdate, ?, ?, ?)";
				PreparedStatement ps = con.prepareStatement(sql,new String[]{"bulletin_id"});
				ps.setString(1, notice.getNoticeTitle());
				ps.setString(2, notice.getNoticeContent());
				ps.setLong(3, notice.getStatus());
				ps.setLong(4, notice.getStaffId());
				ps.setTimestamp(5, notice.getLoseDate() == null?null:new Timestamp(notice.getLoseDate().getTime()));
				ps.setTimestamp(6, notice.getPublishDate() == null?null:new Timestamp(notice.getPublishDate().getTime()));
				ps.setInt(7, notice.getReplyFlag());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}
	
	/**
	 * 修改公告
	 * @param notice
	 */
	public int updateNotice(Notice notice) {
		String sql = "update sys_bulletin set title = ?, content = ?, state = ?, create_time = sysdate, lose_time = ?, release_time = ?, reply_flag = ? where bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, notice.getNoticeTitle(), notice.getNoticeContent(), notice.getStatus(), notice.getLoseDate(), notice.getPublishDate(), notice.getReplyFlag(), notice.getNoticeId());
	}
	
	/**
	 * 查出草稿
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> queryNoticeDraftsByStaffId(long staffId) {
		String sql = "select t.bulletin_id,\n" +
						"       t.title,\n" + 
						"       to_char(t.create_time, 'YYYY-MM-DD HH24:MI') create_time,\n" + 
						"       (select count(*)\n" + 
						"          from sys_file t1\n" + 
						"         where t1.group_code = 'notice'\n" + 
						"           and t1.relation_id = t.bulletin_id\n" + 
						"           and t1.is_del = 0) file_num\n" + 
						"  from sys_bulletin t\n" + 
						" where t.create_staff = ?\n" + 
						"   and t.state = 0\n" + 
						" order by t.create_time desc";
		return this.getJdbcTemplate().query(sql, new RowMapper<NoticeItem>() {

			@Override
			public NoticeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				NoticeItem noticeItem = new NoticeItem();
				noticeItem.setNoticeId(rs.getLong("bulletin_id"));
				noticeItem.setNoticeTitle(rs.getString("title"));
				noticeItem.setFileNum(rs.getInt("file_num"));
				noticeItem.setCreateTime(rs.getString("create_time")==null?"":rs.getString("create_time"));
				return noticeItem;
			}
		}, staffId);
	}

	/**
	 * 查出我的公告
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> queryNoticePublishedByStaffId(long staffId, String offline) {
		String sql = "select *\n" +
						"  from (select t.bulletin_id,\n" + 
						"               t.title,\n" + 
						"				t.stick_up,\n" + 
						"               to_char(t.create_time, 'YYYY-MM-DD HH24:MI') create_time,\n" + 
						"               to_char(t.release_time, 'YYYY-MM-DD HH24:MI') publish_time,\n" + 
						"               to_char(t.lose_time, 'YYYY-MM-DD HH24:MI') lose_time,\n" + 
						"               t.read_cnt,\n" + 
						"               t2.operator_name,\n" + 
						"               case t.state\n" + 
						"                 when 2 then\n" + 
						"                  'y'\n" + 
						"                 else\n" + 
						"                  (case\n" + 
						"                    when t.lose_time < ? then\n" + 
						"                     'y'\n" + 
						"                    else\n" + 
						"                     'n'\n" + 
						"                  end)\n" + 
						"               end off_line,\n" + 
						"               (select count(*)\n" + 
						"                  from sys_file t1\n" + 
						"                 where t1.group_code = 'notice'\n" + 
						"                   and t1.relation_id = t.bulletin_id\n" + 
						"                   and t1.is_del = 0) file_num,\n" + 
						"				(select count(*)\n" +
						"   				from sys_bulletin_comment r\n" + 
						"  				  where r.bulletin_id = t.bulletin_id) reply_num,\n" + 
						" 				t.reply_flag\n" + 
						"          from SYS_BULLETIN t, sys_staff t2\n" + 
						"         where t.create_staff = t2.staff_id(+)\n";
						if(staffId != -1) {
							sql += "           and t.create_staff = " + staffId + "\n";
						}
						sql += "           and t.state between 1 and 2) t1 ";
						if(CommonUtil.hasValue(offline)) {
							sql += "where t1.off_line ='" + offline + "'\n";
						}
						sql += "order by t1.stick_up desc, t1.create_time desc";
		return this.getJdbcTemplate().query(sql, new RowMapper<NoticeItem>() {

			@Override
			public NoticeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				NoticeItem noticeItem = new NoticeItem();
				noticeItem.setNoticeId(rs.getLong("bulletin_id"));
				noticeItem.setNoticeTitle(rs.getString("title"));
				noticeItem.setStickFlag(rs.getInt("stick_up"));
				noticeItem.setFileNum(rs.getInt("file_num"));
				noticeItem.setReplyNum(rs.getInt("reply_num"));
				noticeItem.setCreateTime(rs.getString("create_time")==null?"":rs.getString("create_time"));
				noticeItem.setPublishTime(rs.getString("publish_time") == null?"":rs.getString("publish_time"));
				noticeItem.setLoseTime(rs.getString("lose_time")==null?"":rs.getString("lose_time"));
				noticeItem.setVisitNum(rs.getInt("read_cnt"));
				noticeItem.setOffline(rs.getString("off_line"));
				noticeItem.setStaffName(rs.getString("operator_name"));
				noticeItem.setReplyFlag(rs.getInt("reply_flag"));
				return noticeItem;
			}
		}, new Date());
	}
	
	/**
	 * 批量删除公告
	 * @param noticeIds
	 * @param status
	 */
	public void batchDeleteNotice(final long[] noticeIds) {
		String sql = "delete from sys_bulletin where bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, noticeIds[i]);
			}
			@Override
			public int getBatchSize() {
				return noticeIds.length;
			}});
	}
	
	/**
	 * 批量修改公告至删除状态
	 * @param noticeIds
	 * @param status
	 */
	public void batchUpdateToDeletedStatus(final long[] noticeIds) {
		String sql = "update sys_bulletin t set t.state = 3 where t.bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, noticeIds[i]);
			}
			@Override
			public int getBatchSize() {
				return noticeIds.length;
			}});
	}
	
	/**
	 * 批量修改公告至发布状态
	 * @param noticeIds
	 * @param status
	 */
	public void batchUpdateToPublishedStatus(final long[] noticeIds) {
		String sql = "update sys_bulletin t set t.state = 1, t.release_time = sysdate where t.bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, noticeIds[i]);
			}
			@Override
			public int getBatchSize() {
				return noticeIds.length;
			}});
	}
	
	/**
	 * 批量修改公告至草稿状态
	 * @param noticeIds
	 * @param status
	 */
	public void batchUpdateToDraftsStatus(final long[] noticeIds) {
		String sql = "update sys_bulletin t set t.state = 0, t.release_time = null, t.create_time = sysdate, t.read_cnt = 0 where t.bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter () {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, noticeIds[i]);
			}
			@Override
			public int getBatchSize() {
				return noticeIds.length;
			}});
	}
	
	/**
	 * 根据ID查询
	 * @param noticeId
	 * @return
	 */
	public Notice queryNoticeById(long noticeId) {
		String sql = "select t1.bulletin_id,\n" +
						"       t1.create_staff,\n" + 
						"       t1.title,\n" + 
						"       t1.content,\n" + 
						"       t1.reply_flag,\n" + 
						"       to_char(t1.lose_time, 'YYYY-MM-DD HH24:MI') lose_time,\n" + 
						"       to_char(t1.release_time, 'YYYY-MM-DD HH24:MI') release_time,\n" + 
						"       t1.reply_flag,\n" + 
						"       t2.operator_name staff_name\n" + 
						"  from sys_bulletin t1, sys_staff t2\n" + 
						" where t1.create_staff = t2.staff_id(+)\n" + 
						" and t1.bulletin_id = ?";

		List<Notice> notices = this.getJdbcTemplate().query(sql, new RowMapper<Notice>() {
			
			@Override
			public Notice mapRow(ResultSet rs, int rowNum) throws SQLException {
				Notice notice = new Notice();
				notice.setNoticeId(rs.getLong("bulletin_id"));
				notice.setStaffId(rs.getLong("create_staff"));
				notice.setNoticeTitle(rs.getString("title"));
				notice.setNoticeContent(rs.getString("content"));
				notice.setReplyFlag(rs.getInt("reply_flag"));
				notice.setLoseTime(rs.getString("lose_time"));
				notice.setPublishTime(rs.getString("release_time"));
				notice.setReplyFlag(rs.getInt("reply_flag"));
				notice.setStaffName(rs.getString("staff_name"));
				return notice;
			}
		}, noticeId);
		return notices.size()>0 ? notices.get(0) : null;
	}
	
	/**
	 * 更改至下线状态
	 * @param noticeId
	 * @return
	 */
	public int updateStatus(long noticeId, int status, Date loseDate) {
		String sql = "update sys_bulletin t set t.state = ?, t.lose_time = ? where t.bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, status, loseDate, noticeId);
	}
	
	/**
	 * 查出首页的公告
	 * @param staffId
	 * @param date
	 * @return
	 */
	public List<NoticeItem> queryAllByStaffId(long staffId, Date date) {
		String sql = "select t.bulletin_id,\n" +
						"       t.title,\n" + 
						"       t.stick_up,\n" + 
						"       (select count(*)\n" + 
						"          from sys_file t1\n" + 
						"         where t1.group_code = 'notice'\n" + 
						"           and t1.relation_id = t.bulletin_id\n" + 
						"           and t1.is_del = 0) file_num,\n" + 
						"       to_char(t.release_time, 'YYYY-MM-DD HH24:MI') release_time\n" + 
						"  from sys_bulletin t\n" + 
						" where t.state = 1\n" + 
						"   and (t.lose_time > ? or t.lose_time is null)\n" + 
						" order by t.stick_up desc, t.release_time desc";
		return this.getJdbcTemplate().query(sql, new RowMapper<NoticeItem>(){
			
			@Override
			public NoticeItem mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				NoticeItem noticeItem = new NoticeItem();
				noticeItem.setNoticeId(rs.getLong("bulletin_id"));
				noticeItem.setNoticeTitle(rs.getString("title"));
				noticeItem.setStickFlag(rs.getInt("stick_up"));
				noticeItem.setFileNum(rs.getInt("file_num"));
				noticeItem.setPublishTime(rs.getString("release_time"));
				return noticeItem;
			}}, date);
	}
	
	/**
	 * 修改阅读次数
	 * @param noticeId
	 * @return
	 */
	public int updateVisitNum(long noticeId) {
		String sql = "update sys_bulletin t set t.read_cnt = t.read_cnt+1 where t.bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, noticeId);
	}
	
	/**
	 * 修改置顶标识
	 * @param noticeId
	 * @return
	 */
	public int updateStickById(long noticeId, int stickFlag) {
		String sql = "update sys_bulletin t set t.stick_up = ? where t.bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, stickFlag, noticeId);
	}
	
	/**
	 * 修改回复标识
	 * @param noticeId
	 * @return
	 */
	public int updateReplyById(long noticeId, int replyFlag) {
		String sql = "update sys_bulletin t set t.reply_flag = ? where t.bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, replyFlag, noticeId);
	}
}
