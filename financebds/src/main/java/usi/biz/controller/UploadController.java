package usi.biz.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import usi.biz.service.UploadService;
import usi.sys.dto.PageObj;


@Controller
@RequestMapping("/attachment")
public class UploadController {

	@Resource
	private UploadService uploadService;
	/**
	 * 附件上传的页面
	 * @return
	 */
	@RequestMapping( value="/upload.do",method= RequestMethod.GET)
	public String uploadFile(){
		return "upload/upload";
	}
	
	@RequestMapping( value="/saveUpload.do",method= RequestMethod.POST)
	@ResponseBody
	public String saveUpload(@RequestParam("logo") MultipartFile logo){
		try {
			uploadService.saveUpload(logo);
			 return "success";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "fail";
		} 
	}
	
	/**
	 * 显示下载文件列表
	 * @param session
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping( value="/downLoadList.do",method= RequestMethod.GET)
	public String downloadList(HttpSession session, HttpServletRequest request,Model model){
		return "upload/downloadList";
	}
	
	/**
	 * 下载文件
	 * @param getAllJob
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value="/getPagedDownloadList.do",method= RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getPagedUploadList(PageObj getAllJob,String realName,String startTime,String endTime){
		return uploadService.getPagedUploadList(getAllJob,realName,startTime,endTime);
	}

	/**
	 * 下载功能
	 * @param saveName
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/downloadAttachment.do",method= RequestMethod.GET)
	public void downloadAttachment(String saveName, HttpServletRequest request,
			HttpServletResponse response){  
		String filename= saveName.substring(saveName.lastIndexOf("/")+1);
		FileInputStream fis = null;
		try {
			OutputStream out = response.getOutputStream();
			File file = new File(saveName);
			if(file.exists()){
				response.setContentType("application/DOWLOAD");   
			    response.setHeader("Content-disposition", "attachment;filename=" +filename ); 
				fis = new FileInputStream(file);
				byte[] b = new byte[fis.available()];
				fis.read(b);
				out.write(b);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
