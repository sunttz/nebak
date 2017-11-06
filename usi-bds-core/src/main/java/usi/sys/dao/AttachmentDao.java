package usi.sys.dao;

import java.util.List;

import usi.sys.entity.FileMeta;
import usi.sys.entity.SysFile;
import usi.sys.entity.SysFileOpLog;

public interface AttachmentDao{

	/**
	 * 保存附件信息
	 * @param sysFile 待保存的附件信息对象
	 * @return 附件主键
	 */
	public long saveAttachment(final SysFile sysFile);
	
	/**
	 * 根据主键查询附件的路径
	 * @param fileId 附件主键
	 * @return 文件的路径
	 */
	public String getAbsolutepathById(long fileId);
	
	/**
	 * 根据主键查询附件的路径，文件名
	 * @param fileId 附件主键
	 * @return 
	 */
	public FileMeta getFileMetaById(long fileId);
	
	/**
	 * 根据主键更新附件删除标志位为删除状态1
	 * @param fileId 附件主键
	 * @return 更新记录个数
	 */
	public int delFileByID(long fileId);
	
	/**
	 * 保存操作附件
	 * @param sysFileOpLog 操作附件对象实体
	 * @return 保存记录个数
	 */
	public int saveSysFileOpLog(SysFileOpLog sysFileOpLog);
	
	/**
	 * 根据所属分组和关联主键取附件列表
	 * @param groupCode 附件所属分组
	 * @param relationId 关联主键（业务表的）
	 * @return 附件列表
	 */
	public List<FileMeta> getUploadedFiles(String groupCode,long relationId);
	
	/**
	 * 根据主键更新下载次数
	 * @param fileId 附件主键
	 * @return 更新的记录个数
	 */
	public int incDownloadTimesById(long fileId);
	
	/**
	 * 根据所属分组和关联主键数组取附件列表
	 * @param groupCode 附件所属分组
	 * @param relationIds 关联主键（业务表的）
	 * @return 附件列表
	 */
	public List<FileMeta> queryUploadedFiles(String groupCode,long[] relationIds);
	
	/**
	 * 删除状态1
	 * @param fileIds 附件主键
	 * @return 更新记录个数
	 */
	public int delFileByIds(long[] fileIds);
	
	public void batchSaveSysFileOpLog(final SysFileOpLog[] sysFileOpLogs);
}
