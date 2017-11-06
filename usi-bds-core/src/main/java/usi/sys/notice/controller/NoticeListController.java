package usi.sys.notice.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import usi.sys.dto.AuthInfo;
import usi.sys.notice.service.NoticeService;
import usi.sys.util.ConstantUtil;

@Controller
@RequestMapping("/noticeList")
public class NoticeListController {
	
	@Resource
	private NoticeService noticeService;
	
	@RequestMapping(value = "/main.do", method = RequestMethod.GET)
	public String getMain(HttpSession session, Model model) {
		AuthInfo authInfo = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
		model.addAttribute("noticeItems", noticeService.getAllByStaffId(authInfo.getStaffId()));
		return "system/notice/notice-list";
	}
}
