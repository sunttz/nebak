package usi.sys.dto;

public class NoticeItem {
	
	private long noticeId;
	
	private String noticeTitle;
	
	/**
	 * 附件数量
	 */
	private int fileNum;
	
	/**
	 * 浏览次数
	 */
	private int visitNum;
	
	/**
	 * 回复次数
	 */
	private int replyNum = 0;
	
	/**
	 * 下线标识("y"：下线，"n"：在线)
	 */
	private String offline;
	
	private String createTime;

	private String publishTime;
	
	private String loseTime;
	
	/**
	 * 发布人
	 */
	private String staffName;
	
	/**
	 * 置顶标识
	 */
	private int stickFlag;
	
	/**
	 * 是否可恢复(1：可以，0：不可以)
	 */
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

	public int getFileNum() {
		return fileNum;
	}

	public void setFileNum(int fileNum) {
		this.fileNum = fileNum;
	}

	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public int getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitNum = visitNum;
	}

	public int getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}

	public String getOffline() {
		return offline;
	}

	public void setOffline(String offline) {
		this.offline = offline;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public int getStickFlag() {
		return stickFlag;
	}

	public void setStickFlag(int stickFlag) {
		this.stickFlag = stickFlag;
	}

	public int getReplyFlag() {
		return replyFlag;
	}

	public void setReplyFlag(int replyFlag) {
		this.replyFlag = replyFlag;
	}
}
