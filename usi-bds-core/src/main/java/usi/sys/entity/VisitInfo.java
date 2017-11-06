package usi.sys.entity;

public class VisitInfo {

	private long visitId;
	
	private long noticeId;
	
	private long staffId;
	
	private int visitNum;

	public VisitInfo(long noticeId, long staffId) {
		this.noticeId = noticeId;
		this.staffId = staffId;
	}

	public long getVisitId() {
		return visitId;
	}

	public void setVisitId(long visitId) {
		this.visitId = visitId;
	}

	public long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(long noticeId) {
		this.noticeId = noticeId;
	}

	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public int getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitNum = visitNum;
	}
}
