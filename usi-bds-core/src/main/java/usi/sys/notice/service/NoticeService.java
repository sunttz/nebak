package usi.sys.notice.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dao.AttachmentDao;
import usi.sys.dto.AuthInfo;
import usi.sys.dto.NoticeItem;
import usi.sys.entity.FileMeta;
import usi.sys.entity.Notice;
import usi.sys.entity.SysFileOpLog;
import usi.sys.entity.VisitInfo;
import usi.sys.notice.dao.NoticeDao;
import usi.sys.notice.dao.VisitInfoDao;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;

@Service
@Transactional
public class NoticeService {
	//日志
	private static Logger logger = LoggerFactory.getLogger(NoticeService.class);
	@Resource
	private NoticeDao noticeDao;
	
	@Resource
	private AttachmentDao attachmentDao;
	
	@Resource
	private VisitInfoDao visitInfoDao;
	
	/**
	 * 保存
	 * @param notice
	 * @return 主键
	 */
	public long saveNotice(Notice notice) {
		
		String loseTime = notice.getLoseTime();
		if("".equals(loseTime)) {
			notice.setLoseDate(null);
		} else {
			notice.setLoseDate(CommonUtil.parse(loseTime, "yyyy-MM-dd HH:mm"));
		}
		if(notice.getStatus() == 1) {
			//如果是发布，保存发布时间
			notice.setPublishDate(new Date());
		}
		return noticeDao.insertNotice(notice);
	}
	
	/**
	 * 修改
	 * @param notice
	 * @return
	 */
	public int updateNotice(Notice notice) {
		
		String loseTime = notice.getLoseTime();
		if("".equals(loseTime)) {
			notice.setLoseDate(null);
		} else {
			notice.setLoseDate(CommonUtil.parse(loseTime, "yyyy-MM-dd HH:mm"));
		}
		if(notice.getStatus() == 1) {
			//如果是发布，保存发布时间
			notice.setPublishDate(new Date());
		}
		return noticeDao.updateNotice(notice);
	}
	
	/**
	 * 查出所有的草稿
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> getDraftsByStaffId(long staffId) {
		
		return noticeDao.queryNoticeDraftsByStaffId(staffId);
	}
	
	/**
	 * 查出我的公告
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> getPublishedByStaffId(long staffId, String offline) {
		
		return noticeDao.queryNoticePublishedByStaffId(staffId, offline);
	}
	
	/**
	 * 删除草稿
	 * @param noticeIds
	 * @param authInfo
	 */
	public void removeDrafts(long[] noticeIds, AuthInfo authInfo) {
		//先批量改公告表
		noticeDao.batchDeleteNotice(noticeIds);
		//得到所有的附件
		List<FileMeta> fileMetas = attachmentDao.queryUploadedFiles(ConstantUtil.NOTICE_FILE_GROUP_CODE, noticeIds);
		int n = fileMetas.size();
		long[] fileIds = new long[n];
		SysFileOpLog[] sysFileOpLogs = new SysFileOpLog[n];
		for(int i = 0; i < n; i++) {
			fileIds[i] = fileMetas.get(i).getFileId();
			SysFileOpLog log = new SysFileOpLog();
			log.setFileId(fileMetas.get(i).getFileId());
			log.setStaffId(authInfo.getStaffId());
			log.setOperatorName(authInfo.getUserName());
			log.setOrgId(authInfo.getOrgId());
			log.setOrgName(authInfo.getOrgName());
			log.setOpTime(new Date());
			log.setOpType(0);
			sysFileOpLogs[i] = log;
			
			String filePath = fileMetas.get(i).getFilePath();
			//是否删除成功（默认成功）
			boolean isSucceDel = true;
			if(filePath != null) {
				File file = new File(filePath);
				if (file.isFile() && file.exists()){
					isSucceDel = file.delete();
					if(!isSucceDel) {
						logger.info("删除文件失败。。。文件路径是："+filePath);
					}
				}
			}
		}
		if(fileIds.length > 0) {
			//修改附件表
			attachmentDao.delFileByIds(fileIds);
			//修改附件操作日志表
			attachmentDao.batchSaveSysFileOpLog(sysFileOpLogs);
		}
	}
	
	/**
	 * 批量删除公告
	 * @param noticeIds
	 */
	public void removePublisheds(long[] noticeIds) {
		noticeDao.batchUpdateToDeletedStatus(noticeIds);
	}
	
	/**
	 * 发布公告
	 */
	public void publishNoticeItems(long[] noticeIds) {
		noticeDao.batchUpdateToPublishedStatus(noticeIds);
	}
	
	public Notice getNoticeById(long noticeId) {
		Notice notice = noticeDao.queryNoticeById(noticeId);
		if(notice != null) {
			notice.setFiles(attachmentDao.getUploadedFiles(ConstantUtil.NOTICE_FILE_GROUP_CODE, noticeId));
		}
		return notice;
	}
	
	/**
	 * 拷贝到草稿箱
	 * @param noticeId
	 */
	public void copyToDraft(long noticeId) {
		Notice notice = noticeDao.queryNoticeById(noticeId);
		notice.setStatus(0);
		noticeDao.insertNotice(notice);
	}
	
	/**
	 * 手动下线公告
	 * @param noticeId
	 */
	public Date downlineNotice(long noticeId) {
		Date loseDate = new Date();
		noticeDao.updateStatus(noticeId, 2, loseDate);
		return loseDate;
	}
	
	/**
	 * 重新发布
	 * @param noticeId
	 */
	public void publishNotice(long noticeId, String loseTime) {
		Date loseDate = null;
		if(CommonUtil.hasValue(loseTime)) {
			loseDate = CommonUtil.parse(loseTime, "yyyy-MM-dd HH:mm");
		}
		noticeDao.updateStatus(noticeId, 1, loseDate);
	}
	
	/**
	 * 修改下线时间
	 * @param noticeId
	 * @param loseTime
	 */
	public void updateLoseDate(long noticeId, String loseTime) {
		Date loseDate = null;
		if(CommonUtil.hasValue(loseTime)) {
			loseDate = CommonUtil.parse(loseTime, "yyyy-MM-dd HH:mm");
		}
		noticeDao.updateStatus(noticeId, 1, loseDate);
	}

	/**
	 * 查出某个人的公告
	 * @param staffId
	 * @return
	 */
	public List<NoticeItem> getAllByStaffId(long staffId) {
		return noticeDao.queryAllByStaffId(staffId, new Date());
	}
	
	/**
	 * 记录阅读记录
	 * @param staffId
	 * @param noticeId
	 */
	public void saveVisitLog(long staffId, long noticeId) {
		//修改公告表的阅读次数字段
		noticeDao.updateVisitNum(noticeId);
		//修改记录表
		int n = visitInfoDao.updateVisitNum(noticeId, staffId);
		//如果修改记录为0，添加记录
		if(n < 1) {
			VisitInfo visitInfo = new VisitInfo(noticeId, staffId);
			visitInfoDao.insertVisitInfo(visitInfo);
		}
	}
	
	/**
	 * 置顶公告
	 * @param noticeId
	 */
	public void stickNotice(long noticeId) {
		noticeDao.updateStickById(noticeId, 1);
	}
	
	/**
	 * 取消置顶公告
	 * @param noticeId
	 */
	public void unStickNotice(long noticeId) {
		noticeDao.updateStickById(noticeId, 0);
	}

	/**
	 * 开启回复功能
	 * @param noticeId
	 */
	public void openReply(long noticeId) {
		noticeDao.updateReplyById(noticeId, 1);
	}
	
	/**
	 * 关闭回复功能
	 * @param noticeId
	 */
	public void closeReply(long noticeId) {
		noticeDao.updateReplyById(noticeId, 0);
	}
}
