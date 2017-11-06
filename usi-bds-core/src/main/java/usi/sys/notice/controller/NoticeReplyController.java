package usi.sys.notice.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.entity.NoticeReply;
import usi.sys.notice.service.NoticeReplyService;
import usi.sys.util.CommonUtil;

@Controller
@RequestMapping("/noticeReply")
public class NoticeReplyController {
	
	@Resource
	private NoticeReplyService noticeReplyService;
	
	/**
	 * 发表回复
	 * @param reply
	 * @return
	 */
	@RequestMapping(value = "/publish.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> publish(NoticeReply reply) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Date publishDate = new Date();
			reply.setPublishDate(publishDate);
			long replyId = noticeReplyService.saveReply(reply);
			map.put("flag", true);
			map.put("replyId", replyId);
			map.put("publishTime", CommonUtil.format(publishDate, "yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			map.put("flag", false);
		}
		return map;
	}
}
