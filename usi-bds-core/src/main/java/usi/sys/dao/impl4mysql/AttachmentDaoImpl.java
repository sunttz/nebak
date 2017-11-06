package usi.sys.dao.impl4mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import usi.common.annotation.MysqlDb;
import usi.sys.dao.AttachmentDao;
import usi.sys.dao.JdbcDaoSupport4mysql;
import usi.sys.entity.FileMeta;
import usi.sys.entity.SysFile;
import usi.sys.entity.SysFileOpLog;
/**
 * @author chen.kui
 * @date 2014年9月28日15:28:43
 */
@MysqlDb
@Repository
public class AttachmentDaoImpl extends JdbcDaoSupport4mysql implements
		AttachmentDao {

	@Override
	public long saveAttachment(final SysFile sysFile) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {

				String sql = "insert into sys_file(file_name,absolutepath,file_size,file_type,"
						+ "staff_id,file_time,group_code,relation_id,download_times,is_del)"
						+ " values(?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,
						new String[] { "file_id" });
				ps.setString(1, sysFile.getFileName());
				ps.setString(2, sysFile.getAbsolutepath());
				ps.setLong(3, sysFile.getFileSize());
				ps.setString(4, sysFile.getFileType());
				ps.setLong(5, sysFile.getStaffId());
				ps.setTimestamp(6, new java.sql.Timestamp(sysFile.getFileTime()
						.getTime()));
				ps.setString(7, sysFile.getGroupCode());
				ps.setLong(8, sysFile.getRelationId());
				ps.setLong(9, sysFile.getDownloadTimes());
				ps.setInt(10, sysFile.getIsDel());
				return ps;
			}

		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public String getAbsolutepathById(long fileId) {
		String sql = "select absolutepath from sys_file where file_id=?";
		String tmpPath = null;
		try {
			tmpPath = this.getJdbcTemplate().queryForObject(sql, String.class,
					new Object[] { fileId });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpPath;
	}

	@Override
	public FileMeta getFileMetaById(long fileId) {
		String sql = "select file_name,absolutepath from sys_file where file_id=?";
		List<FileMeta> lstFileMeta = this.getJdbcTemplate().query(sql,
				new Object[] { fileId }, new RowMapper<FileMeta>() {
					public FileMeta mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						FileMeta fileMeta = new FileMeta();
						fileMeta.setFileName(rs.getString("file_name"));
						fileMeta.setFilePath(rs.getString("absolutepath"));
						return fileMeta;
					}
				});
		return lstFileMeta.size() == 0 ? null : lstFileMeta.get(0);
	}

	@Override
	public int delFileByID(long fileId) {
		String sql = "update sys_file set is_del=1 where file_id=?";
		return this.getJdbcTemplate().update(sql, new Object[] { fileId });
	}

	@Override
	public int saveSysFileOpLog(SysFileOpLog sysFileOpLog) {
		String sql = "insert into sys_file_op_log(file_id,"
				+ "staff_id,operator_name,org_id,org_name,"
				+ "op_time,op_type) "
				+ "values(?,?,?,?,?,?,?)";
		return this.getJdbcTemplate().update(
				sql,
				new Object[] { sysFileOpLog.getFileId(),
						sysFileOpLog.getStaffId(),
						sysFileOpLog.getOperatorName(),
						sysFileOpLog.getOrgId(), sysFileOpLog.getOrgName(),
						sysFileOpLog.getOpTime(), sysFileOpLog.getOpType() });
	}

	@Override
	public List<FileMeta> getUploadedFiles(String groupCode, long relationId) {
		String sql = "select file_id,file_name,round(file_size/1024,2)||'KB' file_size,"
				+ "file_type,absolutepath,download_times "
				+ "from sys_file "
				+ "where group_code=? and relation_id=? and is_del=0 order by file_time";
		return this.getJdbcTemplate().query(sql, new Object[]{groupCode,relationId},new RowMapper<FileMeta>() { 
				public FileMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
					FileMeta fileMeta = new FileMeta();
					fileMeta.setFileId(rs.getLong("file_id"));
					fileMeta.setFileName(rs.getString("file_name"));
					fileMeta.setFileSize(rs.getString("file_size"));
					fileMeta.setFileType(rs.getString("file_type"));
					fileMeta.setDownloadTimes(rs.getLong("download_times"));
					fileMeta.setFilePath(rs.getString("absolutepath"));
					return fileMeta;
				}
			} );
	}

	@Override
	public int incDownloadTimesById(long fileId) {
		String sql = "update sys_file set download_times=download_times+1 where file_id=?";
		return this.getJdbcTemplate().update(sql, new Object[]{fileId});
	}

	@Override
	public List<FileMeta> queryUploadedFiles(String groupCode,
			long[] relationIds) {
		String sql = "select file_id, absolutepath "
				+ "from sys_file "
				+ "where group_code = ? and relation_id in " 
				+ Arrays.toString(relationIds).replace("[", "(").replace("]", ")") + " and is_del=0";
		return this.getJdbcTemplate().query(sql, new RowMapper<FileMeta>() { 
				public FileMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
					FileMeta fileMeta = new FileMeta();
					fileMeta.setFileId(rs.getLong("file_id"));
					fileMeta.setFilePath(rs.getString("absolutepath"));
					return fileMeta;
				}
			}, groupCode);
	}

	@Override
	public int delFileByIds(long[] fileIds) {
		String sql = "update sys_file set is_del = 1 where file_id in" 
				+  Arrays.toString(fileIds).replace("[", "(").replace("]", ")");
		return this.getJdbcTemplate().update(sql);
	}

	@Override
	public void batchSaveSysFileOpLog(final SysFileOpLog[] sysFileOpLogs) {
		String sql = "insert into sys_file_op_log(file_id,staff_id,operator_name,org_id,org_name,op_time,op_type) "
				+ "values(?,?,?,?,?,?,?)";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SysFileOpLog sysFileOpLog = sysFileOpLogs[i];
				ps.setLong(1, sysFileOpLog.getFileId());
				ps.setLong(2, sysFileOpLog.getStaffId());
				ps.setString(3, sysFileOpLog.getOperatorName());
				ps.setLong(4, sysFileOpLog.getOrgId());
				ps.setString(5, sysFileOpLog.getOrgName());
				ps.setTime(6, new Time(sysFileOpLog.getOpTime().getTime()));
				ps.setInt(7, sysFileOpLog.getOpType());
			}
			
			@Override
			public int getBatchSize() {
				return sysFileOpLogs.length;
			}
		});
	}

}
