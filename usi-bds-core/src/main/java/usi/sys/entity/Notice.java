package usi.sys.entity;

import java.util.Date;
import java.util.List;

/**
 * 公告实体
 * @author 凡
 *
 */
public class Notice {

	private long noticeId;
	
	private String noticeTitle;
	
	
	private String noticeContent;
	
	/**
	 * 0：草稿，1：发布，2：删除
	 */
	private int status;
	
	private long staffId;
	
	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 失效时间
	 */
	private String loseTime;
	
	private Date loseDate;
	
	/**
	 * 发布时间
	 */
	private String publishTime;
	
	private Date publishDate;
	
	/**
	 * 发布者姓名
	 */
	private String staffName;
	
	private List<FileMeta> files;
	
	private int replyFlag;
	
	public long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(long noticeId) {
		this.noticeId = noticeId;
	}

	public String getNoticeTitle() {
		return noticeTitle;
	}

	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<FileMeta> getFiles() {
		return files;
	}

	public void setFiles(List<FileMeta> files) {
		this.files = files;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getLoseTime() {
		return loseTime;
	}

	public void setLoseTime(String loseTime) {
		this.loseTime = loseTime;
	}

	public Date getLoseDate() {
		return loseDate;
	}

	public void setLoseDate(Date loseDate) {
		this.loseDate = loseDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public int getReplyFlag() {
		return replyFlag;
	}

	public void setReplyFlag(int replyFlag) {
		this.replyFlag = replyFlag;
	}

}
