package usi.sys.entity;

import java.util.Date;

/**
 * 附件表实体
 * @author lmwang
 * 创建时间：2014-4-11 下午1:14:30
 */
public class SysFile {
	
	//附件主键
	private long fileID;
	
	//文件名
	private String fileName;
	
	//文件路径
	private String absolutepath;
	
	//文件大小
	private long fileSize;
	
	//文件类型
	private String fileType;
	
	//上传人
	private long staffId;
	
	//上传时间
	private Date fileTime;
	
	//所属分组
	private String groupCode;
	
	//关联主键
	private long relationId;
	
	//下载次数
	private long downloadTimes;
	
	//是否删除
	private int isDel;

	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAbsolutepath() {
		return absolutepath;
	}

	public void setAbsolutepath(String absolutepath) {
		this.absolutepath = absolutepath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public Date getFileTime() {
		return fileTime;
	}

	public void setFileTime(Date fileTime) {
		this.fileTime = fileTime;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public long getRelationId() {
		return relationId;
	}

	public void setRelationId(long relationId) {
		this.relationId = relationId;
	}

	public long getDownloadTimes() {
		return downloadTimes;
	}

	public void setDownloadTimes(long downloadTimes) {
		this.downloadTimes = downloadTimes;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	
	
	
}
