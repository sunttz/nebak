package usi.sys.entity;


public class FileMeta {

	//附件主键
	private long fileId;
	
	//文件名
	private String fileName;
	
	//文件大小（Kb）
	private String fileSize;
	
	//文件类型
	private String fileType;
	
	//文件下载路径
	private String filePath;
	
	//下载次数
	private long downloadTimes;
	
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getDownloadTimes() {
		return downloadTimes;
	}
	public void setDownloadTimes(long downloadTimes) {
		this.downloadTimes = downloadTimes;
	}
	
}
