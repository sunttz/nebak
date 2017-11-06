package usi.sys.notice.dao.impl4mysql;

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

import usi.common.annotation.MysqlDb;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.dto.NoticeItem;
import usi.sys.entity.Notice;
import usi.sys.notice.dao.NoticeDao;
import usi.sys.util.CommonUtil;

/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class NoticeDaoImpl extends JdbcDaoSupport4mysql implements NoticeDao {

	@Override
	public long insertNotice(final Notice notice) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				String sql = " insert into sys_bulletin (title, content, state, read_cnt, "
						+ "create_staff, create_time, lose_time, release_time, reply_flag) "
						+ " values (?, ?, ?, 0, ?, now(), ?, ?, ?)";
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "bulletin_id" });
				ps.setString(1, notice.getNoticeTitle());
				ps.setString(2, notice.getNoticeContent());
				ps.setLong(3, notice.getStatus());
				ps.setLong(4, notice.getStaffId());
				ps.setTimestamp(5, notice.getLoseDate() == null ? null : new Timestamp(notice.getLoseDate().getTime()));
				ps.setTimestamp(6, notice.getPublishDate() == null ? null : new Timestamp(notice.getPublishDate().getTime()));
				ps.setInt(7, notice.getReplyFlag());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public int updateNotice(Notice notice) {
		String sql = "update sys_bulletin set title = ?, content = ?, state = ?, create_time = now(), "
				+ "lose_time = ?, release_time = ?, reply_flag = ? where bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, notice.getNoticeTitle(),
				notice.getNoticeContent(), notice.getStatus(),
				notice.getLoseDate(), notice.getPublishDate(),
				notice.getReplyFlag(), notice.getNoticeId());
	}

	@Override
	public List<NoticeItem> queryNoticeDraftsByStaffId(long staffId) {
		String sql = "select t.bulletin_id,"
				+ "       t.title,"
				+ "       date_format(t.create_time, '%Y-%m-%d %h:%i') create_time,"
				+ "       (select count(*) from sys_file t1 "
				+ "         where t1.group_code = 'notice' "
				+ "           and t1.relation_id = t.bulletin_id "
				+ "           and t1.is_del = 0) file_num "
				+ "  from sys_bulletin t where t.create_staff = ? "
				+ "   and t.state = 0 order by t.create_time desc";
		return this.getJdbcTemplate().query(sql, new RowMapper<NoticeItem>() {

			@Override
			public NoticeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				NoticeItem noticeItem = new NoticeItem();
				noticeItem.setNoticeId(rs.getLong("bulletin_id"));
				noticeItem.setNoticeTitle(rs.getString("title"));
				noticeItem.setFileNum(rs.getInt("file_num"));
				noticeItem.setCreateTime(rs.getString("create_time") == null ? ""
						: rs.getString("create_time"));
				return noticeItem;
			}
		}, staffId);
	}

	@Override
	public List<NoticeItem> queryNoticePublishedByStaffId(long staffId,
			String offline) {
		String sql = "select *"
				+ "  from (select t.bulletin_id,"
				+ "               t.title,"
				+ "				t.stick_up,"
				+ "               date_format(t.create_time, '%Y-%m-%d %h:%i') create_time,"
				+ "               date_format(t.release_time, '%Y-%m-%d %h:%i') publish_time,"
				+ "               date_format(t.lose_time, '%Y-%m-%d %h:%i') lose_time,"
				+ "               t.read_cnt,"
				+ "               t2.operator_name,"
				+ "               case t.state"
				+ "                 when 2 then 'y' "
				+ "                 else "
				+ "                  (case when t.lose_time < ? then 'y' "
				+ "                    else 'n' "
				+ "                  end) "
				+ "               end off_line, "
				+ "               (select count(*) "
				+ "                  from sys_file t1"
				+ "                 where t1.group_code = 'notice' "
				+ "                   and t1.relation_id = t.bulletin_id "
				+ "                   and t1.is_del = 0) file_num,"
				+ "				(select count(*)"
				+ "   				from sys_bulletin_comment r"
				+ "  				  where r.bulletin_id = t.bulletin_id) reply_num,"
				+ " 				t.reply_flag "
				+ "          from sys_bulletin t left join sys_staff t2 on t.create_staff = t2.staff_id where 1=1 ";
		if (staffId != -1) {
			sql += "           and t.create_staff = " + staffId + "\n";
		}
		sql += "           and t.state between 1 and 2) t1 ";
		if (CommonUtil.hasValue(offline)) {
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
				noticeItem.setCreateTime(rs.getString("create_time") == null ? ""
						: rs.getString("create_time"));
				noticeItem.setPublishTime(rs.getString("publish_time") == null ? ""
						: rs.getString("publish_time"));
				noticeItem.setLoseTime(rs.getString("lose_time") == null ? ""
						: rs.getString("lose_time"));
				noticeItem.setVisitNum(rs.getInt("read_cnt"));
				noticeItem.setOffline(rs.getString("off_line"));
				noticeItem.setStaffName(rs.getString("operator_name"));
				noticeItem.setReplyFlag(rs.getInt("reply_flag"));
				return noticeItem;
			}
		}, new Date());
	}

	@Override
	public void batchDeleteNotice(final long[] noticeIds) {
		String sql = "delete from sys_bulletin where bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, noticeIds[i]);
					}

					@Override
					public int getBatchSize() {
						return noticeIds.length;
					}
				});
	}

	@Override
	public void batchUpdateToDeletedStatus(final long[] noticeIds) {
		String sql = "update sys_bulletin set state = 3 where bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, noticeIds[i]);
					}
					@Override
					public int getBatchSize() {
						return noticeIds.length;
					}
				});

	}

	@Override
	public void batchUpdateToPublishedStatus(final long[] noticeIds) {
		String sql = "update sys_bulletin set state = 1, release_time = now() where bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, noticeIds[i]);
					}

					@Override
					public int getBatchSize() {
						return noticeIds.length;
					}
				});

	}

	@Override
	public void batchUpdateToDraftsStatus(final long[] noticeIds) {
		String sql = "update sys_bulletin set state = 0, release_time = null, create_time = now(), "
				+ "read_cnt = 0 where bulletin_id = ?";
		this.getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, noticeIds[i]);
					}

					@Override
					public int getBatchSize() {
						return noticeIds.length;
					}
				});

	}

	@Override
	public Notice queryNoticeById(long noticeId) {
		String sql = "select t1.bulletin_id,"
				+ "       t1.create_staff,"
				+ "       t1.title,"
				+ "       t1.content,"
				+ "       t1.reply_flag,"
				+ "       date_format(t1.lose_time, '%y-%m-%d %h:%i') lose_time,"
				+ "       date_format(t1.release_time, '%y-%m-%d %h:%i') release_time,"
				+ "       t1.reply_flag,"
				+ "       t2.operator_name staff_name "
				+ "  from sys_bulletin t1 left join sys_staff t2 on t1.create_staff = t2.staff_id"
				+ " where t1.bulletin_id = ?";

		List<Notice> notices = this.getJdbcTemplate().query(sql,
				new RowMapper<Notice>() {

					@Override
					public Notice mapRow(ResultSet rs, int rowNum)
							throws SQLException {
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
		return notices.size() > 0 ? notices.get(0) : null;
	}

	@Override
	public int updateStatus(long noticeId, int status, Date loseDate) {
		String sql = "update sys_bulletin t set t.state = ?, t.lose_time = ? where t.bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, status, loseDate, noticeId);
	}

	@Override
	public List<NoticeItem> queryAllByStaffId(long staffId, Date date) {
		String sql = "select t.bulletin_id,"
				+ "       t.title,"
				+ "       t.stick_up,"
				+ "       (select count(*)"
				+ "          from sys_file t1"
				+ "         where t1.group_code = 'notice'"
				+ "           and t1.relation_id = t.bulletin_id"
				+ "           and t1.is_del = 0) file_num,"
				+ "       date_format(t.release_time, '%y-%m-%d %h:%i') release_time "
				+ "  from sys_bulletin t " + " where t.state = 1 "
				+ "   and (t.lose_time > ? or t.lose_time is null) "
				+ " order by t.stick_up desc, t.release_time desc";
		return this.getJdbcTemplate().query(sql, new RowMapper<NoticeItem>() {

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
			}
		}, date);
	}

	@Override
	public int updateVisitNum(long noticeId) {
		String sql = "update sys_bulletin set read_cnt = read_cnt+1 where bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, noticeId);
	}

	@Override
	public int updateStickById(long noticeId, int stickFlag) {
		String sql = "update sys_bulletin set stick_up = ? where bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, stickFlag, noticeId);
	}

	@Override
	public int updateReplyById(long noticeId, int replyFlag) {
		String sql = "update sys_bulletin set reply_flag = ? where bulletin_id = ?";
		return this.getJdbcTemplate().update(sql, replyFlag, noticeId);
	}

}
