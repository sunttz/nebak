package usi.sys.entity;

import java.util.Date;

/**
 * 公告回复
 * @author 凡
 *
 */
public class NoticeReply {
	
	private long replyId;
	
	private long noticeId;
	
	private long staffId;
	
	private String userName;

	private String orgName;
	
	private String replyContent;

	//发表时间
	private String publishTime;

	private Date publishDate;
		
	public long getReplyId() {
		return replyId;
	}

	public void setReplyId(long replyId) {
		this.replyId = replyId;
	}

	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(long noticeId) {
		this.noticeId = noticeId;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
}
