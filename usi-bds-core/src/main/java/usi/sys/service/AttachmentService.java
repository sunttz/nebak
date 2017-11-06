package usi.sys.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import usi.sys.dao.AttachmentDao;
import usi.sys.dto.AuthInfo;
import usi.sys.entity.FileMeta;
import usi.sys.entity.SysFile;
import usi.sys.entity.SysFileOpLog;
import usi.sys.util.ConfigUtil;

/**
 * 附件上传业务层
 * @author lmwang
 * 创建时间：2014-4-11 下午1:23:30
 */
@Service
public class AttachmentService {
	
	//文件上传的路径
//	private final String UPLOAD =ConfigUtil.getValue("uploadFilePath");
	
	//操作附件表的dao
	@Resource
	private AttachmentDao attachmentDao;
	
	/**
	 * 上传附件并返回上传文件的信息
	 * @param request
	 * @param session
	 * @param groupCode 附件所属分组
	 * @param relationId 关联主键（业务表的）
	 * @return 上传附件的列表信息
	 * @throws Exception 抛出异常方便上传失败时的提示
	 */
	@Transactional(rollbackFor=Exception.class)
	public LinkedList<FileMeta> upload(MultipartHttpServletRequest request,HttpSession session,String groupCode,long relationId) throws Exception{
		
		//获取session里当前登陆人的信息
		AuthInfo authInfo = (AuthInfo)session.getAttribute("authInfo");
		
		LinkedList<FileMeta> files = new LinkedList<FileMeta>();
		//1. 构造一个文件名迭代器
		 Iterator<String> itr =  request.getFileNames();
		 MultipartFile mpf = null;
		 
		 //上传时间
		 Date createTime = new Date();
		 //构造文件路径
		 //绝对路径
		 String fileAbsDir = getFilePath(groupCode,createTime);
		 File realPathDir=new File(fileAbsDir);
		 //是否成功创建目录（默认成功）
		 boolean isSuccMkdirs = true;
		 //如果目录不存在就创建(使用java.io.File 的mkdirs递归建立目录，如果是mkdir的话，只能创建一级目录)
		 if(!realPathDir.exists()){
			 isSuccMkdirs = realPathDir.mkdirs();
			}
		 //2. 迭代每一个文件
		 while(isSuccMkdirs&&itr.hasNext()){
			 
			 //2.1 获取文件
			 mpf = request.getFile(itr.next());
			 //2.11附加非空判断：避免多个附件时，前台可能上传为空的情况
			 if(mpf.isEmpty()) {
				 continue;
			 }

			 //2.2 如果文件多于10个，就pop出一个
			 if(files.size() >= 10)
				 files.pop();
			 
			 //上传文件大小限制为10M
			 if(mpf.getSize()/1024.0/1024.0<=10){
				 //2.3 创建文件元数据
				 FileMeta fileMeta = new FileMeta();
				 fileMeta.setFileName(mpf.getOriginalFilename());
				 fileMeta.setFileSize(mpf.getSize()/1024.0+" Kb");
				 fileMeta.setFileType(mpf.getContentType());
				 
				 //获取文件后缀名前面的点的位置
				 int i=mpf.getOriginalFilename().lastIndexOf(".");
				 //为了防止文件覆盖，上传后使用的文件名格式为：时分秒毫秒+随机四位数+.文件后缀名。
				 String loadedFileName = new SimpleDateFormat("HHmmssSSS").format(createTime)+Math.round(Math.random()*8999+1000)+mpf.getOriginalFilename().substring(i);
				 
				 //构造存放待保存的上传文件信息实体
				 SysFile sysFile = new SysFile();
				 sysFile.setFileName(mpf.getOriginalFilename());
				 sysFile.setAbsolutepath(fileAbsDir+File.separator+loadedFileName);
				 sysFile.setFileSize(mpf.getSize());
				 sysFile.setFileType(mpf.getContentType());
				 sysFile.setStaffId(authInfo.getStaffId());
				 sysFile.setFileTime(createTime);
				 sysFile.setGroupCode(groupCode);
				 sysFile.setRelationId(relationId);
				 sysFile.setDownloadTimes(0);
				 sysFile.setIsDel(0);
				 
				 //保存附件信息并返回附件主键
				 long fileId=attachmentDao.saveAttachment(sysFile);
				 
				 try {
					 
					 //把附件主键放到文件元数据里
					 fileMeta.setFileId(fileId);
					 //2.4 文件元数据信息添加到list
					 files.add(fileMeta);
					// 拷贝文件到servlet上下文的upload目录下，File.separator系统相关路径分隔符
					FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(fileAbsDir+File.separator+loadedFileName));
					
				} catch (IOException e) {
					//异常时把最后一个添加的元素删除
					files.removeLast();
					e.printStackTrace();
					throw new Exception("异常");
				}
			//上传文件大小超过限制	 
			 }else{
				 
				 //通过够则一个文件大小为-1的文件元数据对象返回
				 FileMeta fileMeta = new FileMeta();
				 fileMeta.setFileSize("-1");
				 files.add(fileMeta);
				 
			 }
		 }
		 
		 // 结果可能如下
		 // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
		 //return files;
		 return files;
	}
	
	/**
	 * 删除文件（逻辑删除表记录，物理删除文件）
	 * @param request
	 * @param session
	 * @param fileId 关联主键（业务表的）
	 * @return
	 */
	@Transactional(rollbackFor=Exception.class)
	public String delFile(HttpServletRequest request,HttpSession session,long fileId){
		
		//获取session里当前登陆人的信息
		AuthInfo authInfo = (AuthInfo)session.getAttribute("authInfo");
		
		//获取文件位置
		String filePath = attachmentDao.getAbsolutepathById(fileId);
		//是否删除成功（默认成功）
		boolean isSucceDel = true;
		//删除文件
		if(null!=filePath){
			//取得文件绝对路径
			File file = new File(filePath);
			//是文件且存在则删除
			if (file.isFile() && file.exists()){
				isSucceDel = file.delete();
			}
		}
		if(isSucceDel) {
			SysFileOpLog sysFileOpLog = new SysFileOpLog();
			sysFileOpLog.setFileId(fileId);
			sysFileOpLog.setStaffId(authInfo.getStaffId());
			sysFileOpLog.setOperatorName(authInfo.getUserName());
			sysFileOpLog.setOrgId(authInfo.getOrgId());
			sysFileOpLog.setOrgName(authInfo.getOrgName());
			sysFileOpLog.setOpTime(new Date());
			//操作类型(0删除1下载)
			sysFileOpLog.setOpType(0);
			//更新附件表标识位，标识为已删除并记录附件操作日志
			if(attachmentDao.delFileByID(fileId)==1&&attachmentDao.saveSysFileOpLog(sysFileOpLog)==1){
				return "success";
			}else {
				return "fail";
			}
		}else {
			return "fail";
		}
		
	}
	
	/**
	 * 根据所属分组和关联主键取附件列表
	 * @param groupCode 附件所属分组
	 * @param relationId 关联主键（业务表的）
	 * @return 没有返回null，有的话返回附件元数据列表
	 */
	@Transactional(readOnly = true)
	public List<FileMeta> getUploadedFiles(String groupCode,long relationId){
		
		List<FileMeta> lstFiles = attachmentDao.getUploadedFiles(groupCode, relationId);
		if(lstFiles.size()>0){
			return lstFiles;
		}
		return null;
	}
	
	/**
	 * 记录操作日志，增加下载次数
	 * @param session
	 * @param fileId 附件主键
	 */
	@Transactional(rollbackFor=Exception.class)
	public void getFileService(HttpSession session,long fileId){
		
		//获取session里当前登陆人的信息
		AuthInfo authInfo = (AuthInfo)session.getAttribute("authInfo");
		
		SysFileOpLog sysFileOpLog = new SysFileOpLog();
		sysFileOpLog.setFileId(fileId);
		sysFileOpLog.setStaffId(authInfo.getStaffId());
		sysFileOpLog.setOperatorName(authInfo.getUserName());
		sysFileOpLog.setOrgId(authInfo.getOrgId());
		sysFileOpLog.setOrgName(authInfo.getOrgName());
		sysFileOpLog.setOpTime(new Date());
		//操作类型(0删除1下载)
		sysFileOpLog.setOpType(1);
		attachmentDao.saveSysFileOpLog(sysFileOpLog);
		attachmentDao.incDownloadTimesById(fileId);
		
	}
	
	/**
	 * 根据主键查询附件的路径文件名
	 * @param fileId 附件主键
	 * @return 文件名
	 */
	@Transactional(readOnly = true)
	public FileMeta getFileMetaById(long fileId){
		return attachmentDao.getFileMetaById(fileId);
	}
	
	/**
	 * 根据upload和groupCode和日期生成文件目录
	 * @param groupCode 所属分组
	 * @param date 当前日期
	 * @return
	 */
	public String getFilePath(String groupCode, Date date) {
		String upload =ConfigUtil.getValue("uploadFilePath");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(java.util.Calendar.YEAR);
		int month = calendar.get(java.util.Calendar.MONTH) + 1;
		int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
		//文件路径 upload/groupCode/年月日
		String filePath = upload +File.separator+ groupCode+File.separator+year+month+day;
		return filePath;
	}

}
