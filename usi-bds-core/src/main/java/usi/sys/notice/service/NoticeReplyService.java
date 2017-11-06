package usi.sys.notice.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.entity.NoticeReply;
import usi.sys.notice.dao.NoticeReplyDao;


@Service
@Transactional
public class NoticeReplyService {
	
	@Resource
	private NoticeReplyDao replyDao;
	
	/**
	 * 保存回复
	 * @param reply
	 * @return
	 */
	public long saveReply(NoticeReply reply) {
		
		return replyDao.insertReply(reply);
	}
	
	/**
	 * 查出某个公告的回复
	 * @param noticeId
	 * @return
	 */
	public List<NoticeReply> getReplysByNoticeId(long noticeId) {
		
		return replyDao.queryReplysByNoticeId(noticeId);
	}
}
