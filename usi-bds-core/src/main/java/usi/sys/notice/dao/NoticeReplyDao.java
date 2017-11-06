package usi.sys.notice.dao;

import java.util.List;

import usi.sys.entity.NoticeReply;

/**
 * 公告回复DAO
 * @author 凡
 *
 */
public interface NoticeReplyDao {
	
	/**
	 * 插入一条公告回复
	 * @return
	 */
	public long insertReply(final NoticeReply reply);
	
	/**
	 * 查出某个公告的回复
	 * @param noticeId
	 * @return
	 */
	public List<NoticeReply> queryReplysByNoticeId(long noticeId);
	
}
