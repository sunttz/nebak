package usi.sys.dao.impl4oracle;

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

import usi.common.annotation.OracleDb;
import usi.sys.dao.AttachmentDao;
import usi.sys.dao.JdbcDaoSupport4oracle;
import usi.sys.entity.FileMeta;
import usi.sys.entity.SysFile;
import usi.sys.entity.SysFileOpLog;

/**
 * 附件上传数据库操作（操作表sys_file）
 * @author lmwang
 * 创建时间：2014-4-11 下午1:24:31
 */
@OracleDb
@Repository
public class AttachmentDaoImpl extends JdbcDaoSupport4oracle implements AttachmentDao{

	/**
	 * 保存附件信息
	 * @param sysFile 待保存的附件信息对象
	 * @return 附件主键
	 */
	public long saveAttachment(final SysFile sysFile){

		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){
	
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				
				String sql = "insert into sys_file(file_id,file_name,absolutepath,file_size,file_type,staff_id,file_time,group_code,relation_id,download_times,is_del)" +
						" values(sys_file_seq.nextval,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = con.prepareStatement(sql,new String[]{"FILE_ID"});
				ps.setString(1, sysFile.getFileName());
				ps.setString(2, sysFile.getAbsolutepath());
				ps.setLong(3, sysFile.getFileSize());
				ps.setString(4, sysFile.getFileType());
				ps.setLong(5, sysFile.getStaffId());
				/**java.sql.Date只有年月日，java.sql.Timestamp年月日时分秒
				 * 所以往数据库里保存年月日时分秒，需要用new java.sql.Timestamp(date.getTime())构造一个出来，其中date是java.util.Date（这个是包含年月日时分秒的）。
				 */
				ps.setTimestamp(6,new java.sql.Timestamp(sysFile.getFileTime().getTime()));
				ps.setString(7,sysFile.getGroupCode());
				ps.setLong(8, sysFile.getRelationId());
				ps.setLong(9, sysFile.getDownloadTimes());
				ps.setInt(10, sysFile.getIsDel());
				return ps;
			}
			
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}
	
	/**
	 * 根据主键查询附件的路径
	 * @param fileId 附件主键
	 * @return 文件的路径
	 */
	public String getAbsolutepathById(long fileId){
		
		String sql = "select absolutepath from sys_file where file_id=?";
		String tmpPath=null;
		try{
			tmpPath = this.getJdbcTemplate().queryForObject(sql, String.class, new Object[]{fileId});
		}catch(Exception e){
			e.printStackTrace();
		}
		return tmpPath;
		
	}
	
	/**
	 * 根据主键查询附件的路径，文件名
	 * @param fileId 附件主键
	 * @return 
	 */
	public FileMeta getFileMetaById(long fileId){
		
		String sql = "select file_name,absolutepath from sys_file where file_id=?";
		List<FileMeta> lstFileMeta = this.getJdbcTemplate().query(sql, new Object[]{fileId},new RowMapper<FileMeta>() { 
			public FileMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
				FileMeta fileMeta = new FileMeta();
				fileMeta.setFileName(rs.getString("file_name"));
				fileMeta.setFilePath(rs.getString("absolutepath"));
				return fileMeta;
			}
		} );
		return lstFileMeta.size() == 0?null:lstFileMeta.get(0);
		
	}
	
	/**
	 * 根据主键更新附件删除标志位为删除状态1
	 * @param fileId 附件主键
	 * @return 更新记录个数
	 */
	public int delFileByID(long fileId){
		
		String sql = "update sys_file set is_del=1 where file_id=?";
		return this.getJdbcTemplate().update(sql, new Object[]{fileId});
		
	}
	
	/**
	 * 保存操作附件
	 * @param sysFileOpLog 操作附件对象实体
	 * @return 保存记录个数
	 */
	public int saveSysFileOpLog(SysFileOpLog sysFileOpLog){
		
		String sql = "insert into sys_file_op_log(op_log_id,file_id,staff_id,operator_name,org_id,org_name,op_time,op_type) values(sys_file_op_log_seq.nextval,?,?,?,?,?,?,?)";
		return this.getJdbcTemplate().update(sql, new Object[]{
				sysFileOpLog.getFileId(),sysFileOpLog.getStaffId(),sysFileOpLog.getOperatorName(),sysFileOpLog.getOrgId(),
				sysFileOpLog.getOrgName(),sysFileOpLog.getOpTime(),sysFileOpLog.getOpType()
				});
		
	}
	
	/**
	 * 根据所属分组和关联主键取附件列表
	 * @param groupCode 附件所属分组
	 * @param relationId 关联主键（业务表的）
	 * @return 附件列表
	 */
	public List<FileMeta> getUploadedFiles(String groupCode,long relationId){
		
		String sql = "select file_id,file_name,round(file_size/1024,2)||'Kb' file_size,file_type,absolutepath,download_times from sys_file where group_code=? and relation_id=? and is_del=0 order by file_time";
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
	
	/**
	 * 根据主键更新下载次数
	 * @param fileId 附件主键
	 * @return 更新的记录个数
	 */
	public int incDownloadTimesById(long fileId){
		
		String sql = "update sys_file set download_times=download_times+1 where file_id=?";
		return this.getJdbcTemplate().update(sql, new Object[]{fileId});
		
	}
	
	/**
	 * 根据所属分组和关联主键数组取附件列表
	 * @param groupCode 附件所属分组
	 * @param relationIds 关联主键（业务表的）
	 * @return 附件列表
	 */
	public List<FileMeta> queryUploadedFiles(String groupCode,long[] relationIds){
		
		String sql = "select file_id, absolutepath from sys_file where group_code = ? and relation_id in " + Arrays.toString(relationIds).replace("[", "(").replace("]", ")") + " and is_del = 0";
		return this.getJdbcTemplate().query(sql, new RowMapper<FileMeta>() { 
				public FileMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
					FileMeta fileMeta = new FileMeta();
					fileMeta.setFileId(rs.getLong("file_id"));
					fileMeta.setFilePath(rs.getString("absolutepath"));
					return fileMeta;
				}
			}, groupCode);
	}
	
	/**
	 * 删除状态1
	 * @param fileIds 附件主键
	 * @return 更新记录个数
	 */
	public int delFileByIds(long[] fileIds){
		
		String sql = "update sys_file set is_del = 1 where file_id in" +  Arrays.toString(fileIds).replace("[", "(").replace("]", ")");
		return this.getJdbcTemplate().update(sql);
	}
	
	public void batchSaveSysFileOpLog(final SysFileOpLog[] sysFileOpLogs){
		
		String sql = "insert into sys_file_op_log(op_log_id,file_id,staff_id,operator_name,org_id,org_name,op_time,op_type) values(sys_file_op_log_seq.nextval,?,?,?,?,?,?,?)";
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
