package usi.sys.notice.dao;

import java.util.Date;
import java.util.List;

import usi.sys.dto.NoticeItem;
import usi.sys.entity.Notice;

public interface NoticeDao {

	/**
	 * 插入公告
	 * @param notice
	 * @return 主键
	 */
	public long insertNotice(final Notice notice);
	
	/**
	 * 修改公告
	 * @param notice
	 */
	public int updateNotice(Notice notice);
	
	/**
	 * 查出草稿
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> queryNoticeDraftsByStaffId(long staffId);

	/**
	 * 查出我的公告
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> queryNoticePublishedByStaffId(long staffId, String offline);
	
	/**
	 * 批量删除公告
	 * @param noticeIds
	 * @param status
	 */
	public void batchDeleteNotice(final long[] noticeIds);
	
	/**
	 * 批量修改公告至删除状态
	 * @param noticeIds
	 * @param status
	 */
	public void batchUpdateToDeletedStatus(final long[] noticeIds);
	
	/**
	 * 批量修改公告至发布状态
	 * @param noticeIds
	 * @param status
	 */
	public void batchUpdateToPublishedStatus(final long[] noticeIds);
	
	/**
	 * 批量修改公告至草稿状态
	 * @param noticeIds
	 * @param status
	 */
	public void batchUpdateToDraftsStatus(final long[] noticeIds);
	
	/**
	 * 根据ID查询
	 * @param noticeId
	 * @return
	 */
	public Notice queryNoticeById(long noticeId);
	
	/**
	 * 更改至下线状态
	 * @param noticeId
	 * @return
	 */
	public int updateStatus(long noticeId, int status, Date loseDate);
	
	/**
	 * 查出首页的公告
	 * @param staffId
	 * @param date
	 * @return
	 */
	public List<NoticeItem> queryAllByStaffId(long staffId, Date date);
	
	/**
	 * 修改阅读次数
	 * @param noticeId
	 * @return
	 */
	public int updateVisitNum(long noticeId);
	
	/**
	 * 修改置顶标识
	 * @param noticeId
	 * @return
	 */
	public int updateStickById(long noticeId, int stickFlag);
	
	/**
	 * 修改回复标识
	 * @param noticeId
	 * @return
	 */
	public int updateReplyById(long noticeId, int replyFlag);
}
