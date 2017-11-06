package usi.biz.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import usi.biz.dao.UploadDao;
import usi.biz.entity.Attachment;
import usi.biz.util.PropertyUtil;
import usi.sys.dto.PageObj;

@Service
public class UploadService {

	@Resource
	private UploadDao uploadDao;
	
	public long saveUpload(MultipartFile logo){
		try {
			String realName =logo.getOriginalFilename();
			String extName = realName.substring(realName.lastIndexOf(".")+1).toLowerCase();
			String saveName = this.uploadFile(logo, extName);
			Attachment atta = new Attachment();
			Date  createTime = new Date();
			atta.setCreateTime(createTime);
			atta.setRealName(realName);
			atta.setSaveName(saveName);
			return uploadDao.saveUpload(atta);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
		
	}
	
	
	public String uploadFile(MultipartFile logo,String extName){
		String path="";
		if(extName.matches("gif|jpg|jpeg|png|bmp")){
			path =	PropertyUtil.getStringValue("upload.img.path");
		}else{
			path = PropertyUtil.getStringValue("upload.file.path");
		}
		//文件名处理
		String lastName = System.currentTimeMillis()+"."+extName;
		//保存路径
		File fileDir =  new File(path);
		if(!fileDir.exists()){
		    fileDir.mkdirs();
		}
		try {
			//保存文件
			FileCopyUtils.copy(logo.getBytes(),new File(fileDir,lastName));
			return path+"/"+lastName;
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
			
		} 
	}
	
	public Map<String,Object> getPagedUploadList(PageObj pageObj,String realName,String startTime,String endTime){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Attachment> list = uploadDao.downloadList(pageObj,realName,startTime,endTime);
		map.put("total", pageObj.getTotal());
		map.put("rows", list);
		return map;
	}
}
