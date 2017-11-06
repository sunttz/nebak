package usi.sys.notice.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.NoticeItem;
import usi.sys.entity.Notice;
import usi.sys.entity.NoticeReply;
import usi.sys.notice.service.NoticeReplyService;
import usi.sys.notice.service.NoticeService;
import usi.sys.service.AttachmentService;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Resource
	private NoticeService noticeService;
	
	@Resource
	private AttachmentService attachmentService;
	
	@Resource
	private NoticeReplyService noticeReplyService;
	
	/**
	 * 转发到新建公告页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/new.do", method = RequestMethod.GET)
	public String toNew(Model model, Long noticeId) {
		Notice notice = null;
		if(noticeId != null) {
			notice = noticeService.getNoticeById(noticeId);
			if(notice == null) {
				notice = new Notice();
				notice.setNoticeId(-1);
			}
		} else {
			notice = new Notice();
			notice.setNoticeId(-1);
		}
		model.addAttribute("notice", notice);
		return "system/notice/notice-new";
	}
	
	@RequestMapping(value = "/postNotice.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> postNotice(Notice notice) {
		Map<String, Object> msgMap = new HashMap<String, Object>();
		try {
			if(notice.getNoticeId() == -1) {
				msgMap.put("noticeId", noticeService.saveNotice(notice));
			} else {
				noticeService.updateNotice(notice);
				msgMap.put("noticeId", notice.getNoticeId());
			}
			msgMap.put("msgFlag", true);
		} catch (Exception e) {
			e.printStackTrace();
			msgMap.put("msgFlag", false);
		}
		return msgMap;
	}
	
	/**
	 * 转发到草稿箱
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/drafts.do", method = RequestMethod.GET)
	public String toDrafts(Model model) {
		return "system/notice/notice-drafts";
	}
	
	/**
	 * 查出所有的草稿
	 * @param staffId
	 * @return
	 */
	@RequestMapping(value = "/getDrafts.do", method = RequestMethod.GET)
	@ResponseBody
	public List<NoticeItem> getDrafts(long staffId) {
		
		return noticeService.getDraftsByStaffId(staffId);
	}
	
	/**
	 * 删除草稿
	 * @param request
	 * @param session
	 * @param noticeIds
	 * @return
	 */
	@RequestMapping(value = "/removeDrafts.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean removeDrafts(HttpServletRequest request, HttpSession session, @RequestBody long[] noticeIds) {
		try {
			AuthInfo authInfo = (AuthInfo)session.getAttribute("authInfo");
			noticeService.removeDrafts(noticeIds, authInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 发布
	 * @return
	 */
	@RequestMapping(value = "/publishNoticeItems.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean publishNoticeItems(@RequestBody long[] noticeIds) {
		try {
			noticeService.publishNoticeItems(noticeIds);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 转发到已发布
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/toPublished.do", method = RequestMethod.GET)
	public String toPublished(Model model) {
		return "system/notice/notice-published";
	}
	
	/**
	 * 查出所有的已发布公告
	 * @param staffId
	 * @return
	 */
	@RequestMapping(value = "/getPublished.do", method = RequestMethod.GET)
	@ResponseBody
	public List<NoticeItem> getPublished(long staffId, String offline) {
		
		return noticeService.getPublishedByStaffId(staffId, offline);
	}
	
	/**
	 * 删除公告
	 * @param request
	 * @param session
	 * @param noticeIds
	 * @return
	 */
	@RequestMapping(value = "/removePublisheds.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean removePublisheds(@RequestBody long[] noticeIds) {
		try {
			noticeService.removePublisheds(noticeIds);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 拷贝公告到草稿箱
	 * @param request
	 * @param session
	 * @param noticeIds
	 * @return
	 */
	@RequestMapping(value = "/copyToDraft.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean copyToDraft(long noticeId) {
		try {
			noticeService.copyToDraft(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 手动下线公告
	 * @param request
	 * @param session
	 * @param noticeIds
	 * @return
	 */
	@RequestMapping(value = "/downlineNotice.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> downlineNotice(long noticeId) {
		Map<String, Object> msgMap = new HashMap<String, Object>();
		try {
			Date loseDate = noticeService.downlineNotice(noticeId);
			String loseTime = CommonUtil.format(loseDate, "yyyy-MM-dd HH:mm");
			msgMap.put("msgFlag", true);
			msgMap.put("loseTime", loseTime);
		} catch (Exception e) {
			e.printStackTrace();
			msgMap.put("msgFlag", false);
		}
		return msgMap;
	}
	
	/**
	 * 重新发布公告
	 * @param request
	 * @param session
	 * @param noticeIds
	 * @return
	 */
	@RequestMapping(value = "/publishNotice.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean publishNotice(long noticeId, String loseTime) {
		try {
			noticeService.publishNotice(noticeId, loseTime);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 修改下线时间
	 * @param request
	 * @param session
	 * @param noticeIds
	 * @return
	 */
	@RequestMapping(value = "/updateLoseDate.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateLoseDate(long noticeId, String loseTime) {
		try {
			noticeService.updateLoseDate(noticeId, loseTime);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 预览公告
	 * @param notice
	 * @return
	 */
	@RequestMapping(value = "/previewNotice.do", method = RequestMethod.POST)
	public String previewNotice(Notice notice, Model model, HttpSession session) {
		AuthInfo authInfo = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
		if(!CommonUtil.hasValue(notice.getNoticeTitle())) {
			notice.setNoticeTitle("无标题");
		}
		if(notice.getNoticeId() != -1) {
			//查找附件
			notice.setFiles(noticeService.getNoticeById(notice.getNoticeId()).getFiles());
		}
		notice.setPublishTime(CommonUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
		notice.setStaffName(authInfo.getUserName());
		model.addAttribute("notice", notice);
		return "system/notice/notice-preview";
	}
	
	/**
	 * 预览公告
	 * @param notice
	 * @return
	 */
	@RequestMapping(value = "/previewNotice.do", method = RequestMethod.GET)
	public String previewNotice() {
		//TODO get方式的处理
		return "system/notice/notice-preview";
	}
	
	/**
	 * 公告详情
	 * @param noticeId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/readNotice.do", method = RequestMethod.GET)
	public String readNotice(long noticeId, Model model, HttpSession session) {
		Notice notice = noticeService.getNoticeById(noticeId);
		List<NoticeReply> replys = noticeReplyService.getReplysByNoticeId(noticeId);
		model.addAttribute("notice", notice);
		model.addAttribute("replys", replys);
		model.addAttribute("total", replys.size());
		return "system/notice/notice-detail";
	}
	
	/**
	 * 浏览公告（读者阅览）
	 * @return
	 */
	@RequestMapping(value = "/browseNotice.do", method = RequestMethod.GET)
	public String browseNotice(HttpSession session, Model model, long noticeId) {
		AuthInfo authInfo = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
		noticeService.saveVisitLog(authInfo.getStaffId(), noticeId);
		Notice notice = noticeService.getNoticeById(noticeId);
		List<NoticeReply> replys = noticeReplyService.getReplysByNoticeId(noticeId);
		model.addAttribute("notice", notice);
		model.addAttribute("replys", replys);
		model.addAttribute("total", replys.size());
		return "system/notice/notice-browse";
	}
	/**
	 * 上线公告管理
	 * @return
	 */
	@RequestMapping(value = "/toAllPublished.do", method = RequestMethod.GET)
	public String toAllPublished() {
		
		return "system/notice/notice-all";
	}
	
	/**
	 * 置顶公告
	 * @param noticeId
	 * @return
	 */
	@RequestMapping(value = "/stickNotice.do", method = RequestMethod.GET)
	@ResponseBody
	public boolean stickNotice(long noticeId) {
		try {
			noticeService.stickNotice(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 取消置顶
	 * @param noticeId
	 * @return
	 */
	@RequestMapping(value = "/unStickNotice.do", method = RequestMethod.GET)
	@ResponseBody
	public boolean unStickNotice(long noticeId) {
		try {
			noticeService.unStickNotice(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 开启回复
	 * @param noticeId
	 * @return
	 */
	@RequestMapping(value = "/openReply.do", method = RequestMethod.GET)
	@ResponseBody
	public boolean openReply(long noticeId) {
		try {
			noticeService.openReply(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 关闭回复
	 * @param noticeId
	 * @return
	 */
	@RequestMapping(value = "/closeReply.do", method = RequestMethod.GET)
	@ResponseBody
	public boolean closeReply(long noticeId) {
		try {
			noticeService.closeReply(noticeId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
