package usi.biz.service;

import org.springframework.stereotype.Service;
import usi.biz.dao.AutoLogDao;
import usi.biz.dao.NeServerDao;
import usi.biz.entity.AutoLog;
import usi.biz.entity.AutoLogDto;
import usi.biz.entity.NeServer;
import usi.biz.util.ChineseToEnglishUtil;
import usi.biz.util.FtpUtils;
import usi.sys.dto.PageObj;
import usi.sys.util.ConfigUtil;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NeServerService {
	@Resource
	private NeServerDao neServerDao;
	@Resource 
	private AutoLogDao autoLogDao;
	
	private static String rootName=ConfigUtil.getValue("download.file.path");
	
	public List<NeServer> getAllOrg(){
		return neServerDao.getAllOrg();
	}
	/**
	 * 查询所有网元信息（分页）
	 * @return
	 */
	public Map<String,Object> getPageAllNE(PageObj pageObj,Long orgId,String deviceType){
		Map<String,Object> map = new HashMap<String, Object>();
		List<NeServer> list = neServerDao.getPageAllNE(pageObj,orgId,deviceType);
		map.put("total", pageObj.getTotal());
		map.put("rows", list);
		return map;
	}
	/**
	 * 查询所有网元信息（不分页）
	 * @return
	 */
	public List<NeServer> getAllNE(){
		return neServerDao.getAllNE();
	}
	/**
	 * 根据时间删除自动备份日志（当前日期）
	 * @return
	 */
	public void deleteAutoLogByTime(){
		autoLogDao.deleteAutoLogByTime();
	}
	/**
	 * 查询所有网元信息（不分页）
	 * @return
	 */
	public List<AutoLogDto> getAutoResult(String dateTime){
		return neServerDao.getAutoResult(dateTime);
	}
	
	/**
	 * 网元备份
	 * @return
	 */
	public String bakNow(String ids){
		String result="";
		//本地地址 
		String downloadPath="";
		//ftp上文件地址
		String dir="";
		//ftp上要下载的文件名 
		String fileName="";
		//主机名
		String hostname="";
		//端口
		int port=21;
		//用户名
		String username="";
		//密码
		String password="";
		//超时时间
		int activeTime=3000;
		
		//第一步获取单个网元信息
		String[] serverIds=ids.split(",");
		for(int i=0;i<serverIds.length;i++){
			List<NeServer> list = neServerDao.getNeServerById(Long.parseLong(serverIds[i]));
			NeServer neserver=list.get(0);
			downloadPath=checkBakAddr(neserver.getOrgName(),neserver.getDeviceType(),neserver.getDeviceName());
			dir=neserver.getBakPath();
			hostname=neserver.getDeviceAddr();
			username=neserver.getUserName();
			password=neserver.getPassWord();
			System.out.println("==============downloadPath(本机路径):"+downloadPath);
			System.out.println("==============dir(ftp服务器路径):"+dir);
			System.out.println("==============hostname(主机名):"+hostname);
			System.out.println("==============port(端口):"+port);
			System.out.println("==============username(用户名):"+username);
			System.out.println("==============password(密码):"+password);
			try{
				Boolean flag=FtpUtils.fileDownload(downloadPath,dir,fileName,hostname,port, username,password,activeTime);
				System.out.println("==============flag(返回标志位):"+flag);
				if(!flag){
					if(result.equals("")){
						result=serverIds[i];
					}else{
						result+=","+serverIds[i];
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				if(result.equals("")){
					result=serverIds[i];
				}else{
					result+=","+serverIds[i];
				}
			}
		}
		System.out.println("==============result(错误结果):"+result);
		return result;
	}
	
	
	
	/**
	 * 自动网元备份
	 * @return
	 */
	public String autoBakNow(String ids){
		String result="";
		//本地地址 
		String downloadPath="";
		//ftp上文件地址
		String dir="";
		//ftp上要下载的文件名 
		String fileName="";
		//主机名
		String hostname="";
		//端口
		int port=21;
		//用户名
		String username="";
		//密码
		String password="";
		//超时时间
		int activeTime=3000;
		
		//第一步获取单个网元信息
		String[] serverIds=ids.split(",");
		for(int i=0;i<serverIds.length;i++){
			List<NeServer> list = neServerDao.getNeServerById(Long.parseLong(serverIds[i]));
			NeServer neserver=list.get(0);
			downloadPath=checkBakAddr(neserver.getOrgName(),neserver.getDeviceType(),neserver.getDeviceName());
			dir=neserver.getBakPath();
			hostname=neserver.getDeviceAddr();
			username=neserver.getUserName();
			password=neserver.getPassWord();
			System.out.println("==============downloadPath(本机路径):"+downloadPath);
			System.out.println("==============dir(ftp服务器路径):"+dir);
			System.out.println("==============hostname(主机名):"+hostname);
			System.out.println("==============port(端口):"+port);
			System.out.println("==============username(用户名):"+username);
			System.out.println("==============password(密码):"+password);
			try{
				Boolean flag=FtpUtils.fileDownload(downloadPath,dir,fileName,hostname,port, username,password,activeTime);
				System.out.println("==============flag(返回标志位):"+flag);
				if(!flag){
					if(result.equals("")){
						result=serverIds[i];
					}else{
						result+=","+serverIds[i];
					}
				}
				int bakFlag=flag==true?1:0;
				this.addAutoLog(Long.parseLong(serverIds[i]),bakFlag);
			}catch(Exception e){
				e.printStackTrace();
				if(result.equals("")){
					result=serverIds[i];
				}else{
					result+=","+serverIds[i];
				}
			}
		}
		System.out.println("==============result(错误结果):"+result);
		return result;
	}
	
	
	/**
	 * 通过id保存自动备份日志
	 * @return
	 */
	public void addAutoLog(Long serverId,int bakFlag){
		try{
			AutoLog record=new AutoLog();
			record.setServerId(serverId);
			record.setBakFlag(bakFlag);
			autoLogDao.saveAutoLog(record);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 检查备份服务器地址名称是否存在  不存在创建,返回路径，存在不创建
	 * @return
	 */
	public String checkBakAddr(String orgName,String deviceType,String deviceName){
		//获取地市首字母
		String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);
		//获取当前年月日
		String date=getDate();
		String firstFolderName=rootName+File.separator+englishOrgName+"_"+deviceType+"_"+date;
		//检查一级目录是否存在
		File fileFirst = new File(firstFolderName);
		System.out.println("===============fileFirst:"+fileFirst);
		if (!fileFirst.exists()) {
			fileFirst.mkdir();
		}
		//检查二级目录是否存在
		String secondFolderName=firstFolderName+File.separator+englishOrgName+"_"+deviceType+"_"+deviceName+"_"+date;
		File fileSecond=new File(secondFolderName);
		System.out.println("===============fileSecond:"+fileSecond);
		if (!fileSecond.exists()) {
			fileSecond.mkdir();
		}else{
			fileSecond.delete();
			fileSecond.mkdir();
		}
		return secondFolderName;
	}
	/**
	 * 获取当前年月日
	 * @return
	 */
	public String getDate(){
		Calendar now = Calendar.getInstance();  
		String year=String.valueOf(now.get(Calendar.YEAR));
		String month= String.valueOf(now.get(Calendar.MONTH) + 1);
		String day=String.valueOf(now.get(Calendar.DAY_OF_MONTH));
		month=month.length()<2?'0'+month:month;
		day=day.length()<2?'0'+day:day;
		return year+month+day;
	}
	/**
	 * 获取所有网元备份后列表
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String,Object>> getResult(String orgName,String dateTime,String filePath) throws Exception{
		List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
		
		String englishOrgName="";
		//获取地市首字母
		if(orgName!=null && !orgName.equals("")){
			englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);
		}
		//标记位设置  0 无参数  1 有地市名称  2 有时间日期  3 既有地市名称也有时间日期
		int flag=0;
		if(orgName!=null && !orgName.equals("")){
			flag=1;
		}
		if(dateTime!=null && !dateTime.equals("")){
			flag=2;
		}
		if(orgName!=null && !orgName.equals("")&&dateTime!=null && !dateTime.equals("")){
			flag=3;
		}
		System.out.println("===========flag:"+flag);
		File dir =new File(filePath);
		File[] files=dir.listFiles();
		String fileName="";
		for(int i=0;i<files.length;i++){
			File thisFile=files[i];
			Map<String,Object> map=new HashMap<String,Object>();
			fileName=files[i].getName();
			System.out.println("===========file name:"+fileName);
			boolean isDirectory=files[i].isDirectory();
			 //if(files[i].isDirectory()){//是文件夹
				 switch(flag){
					 case 1:{
						 if(fileName.contains(englishOrgName)){
							 //设置地市名称
							 map.put("orgName", orgName);
							 //设置文件夹名称
							 map.put("fileName", fileName);
							 //取得子文件夹个数
							 if(isDirectory){
								 map.put("childFileNum", getlist(files[i]));
							 }
							 //设置文件夹时间
							 long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
							 String ctime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
							 map.put("fileDate", ctime);
							 //设置文件夹大小
							 if(isDirectory){
								 map.put("fileSize", this.getFileSize(thisFile));
							 }else{
								 map.put("fileSize",thisFile.length());
							 }
							 //设置文件路径
							 map.put("filePath",filePath+"/"+fileName);
							 //设置是否文件夹
							 map.put("isDirectory",isDirectory);
							 listMap.add(map);
						 }
						 break;
					 }
					 case 2:{
						 if(fileName.contains(dateTime)){
							 //设置地市名称
							 String[] nameArray=fileName.split("_");
							 map.put("orgName", ChineseToEnglishUtil.getNameByHeadchar(nameArray[0]));
							 //设置文件夹名称
							 map.put("fileName", fileName);
							 //取得子文件夹个数
							 if(isDirectory){
								 map.put("childFileNum", getlist(files[i]));
							 }else{
								 map.put("childFileNum", 0L);
							 }
							 //设置文件夹时间
							 long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
							 String ctime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
							 map.put("fileDate", ctime);
							 //设置文件夹大小
							 if(isDirectory){
								 map.put("fileSize", this.getFileSize(thisFile));
							 }else{
								 map.put("fileSize",thisFile.length());
							 }
							 //设置文件路径
							 map.put("filePath",filePath+"/"+fileName);
							 //设置是否文件夹
							 map.put("isDirectory",isDirectory);
							 listMap.add(map);
						 }
						 break;
					 }
					 case 3:{
						 if(fileName.contains(dateTime)&&fileName.contains(englishOrgName)){
							 //设置地市名称
							 map.put("orgName", orgName);
							 //设置文件夹名称
							 map.put("fileName", fileName);
							 //取得子文件夹个数
							 if(isDirectory){
								 map.put("childFileNum", getlist(files[i]));
							 }else{
								 map.put("childFileNum", 0L);
							 }
							 //设置文件夹时间
							 long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
							 String ctime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
							 map.put("fileDate", ctime);
							 //设置文件夹大小
							 if(isDirectory){
								 map.put("fileSize", this.getFileSize(thisFile));
							 }else{
								 map.put("fileSize",thisFile.length());
							 }
							 //设置文件路径
							 map.put("filePath",filePath+"/"+fileName);
							 //设置是否文件夹
							 map.put("isDirectory",isDirectory);
							 listMap.add(map);
						 }
						 break;
					 }
					 default:{
						 //设置地市名称
						 String[] nameArray=fileName.split("_");
						 map.put("orgName", ChineseToEnglishUtil.getNameByHeadchar(nameArray[0]));
						 //设置文件夹名称
						 map.put("fileName", fileName);
						 //取得子文件夹个数
						 if(isDirectory){
							 map.put("childFileNum", getlist(files[i]));
						 }else{
							 map.put("childFileNum", 0L);
						 }
						 //设置文件夹时间
						 long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
						 String ctime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
						 map.put("fileDate", ctime);
						 //设置文件夹大小
						 if(isDirectory){
							 map.put("fileSize", this.getFileSize(thisFile));
						 }else{
							 map.put("fileSize",thisFile.length());
						 }
						 //设置文件路径
						 map.put("filePath",filePath+"/"+fileName);
						 //设置是否文件夹
						 map.put("isDirectory",isDirectory);
						 listMap.add(map);
					 }
				 }
			 //}
		}
		return listMap;
		
	}
	
	/**
	 * 获取文件夹大小  递归
	 * @return
	 */
    public long getFileSize(File f)throws Exception//取得文件夹大小
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }
    /**
	 * 获取子文件夹个数
	 * @return
	 */
    public long getlist(File f){
        long size = 0;
        File flist[] = f.listFiles();
        size=flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
               size++;
            }
        }
        return size;
    }

	/**
	 * 新增网元
	 * @param neServer
	 */
	public int saveNeServer(NeServer neServer){
		return neServerDao.saveNeServer(neServer);
	}

	/**
	 * 修改网元
	 * @param neServer
	 * @return
	 */
	public int updateNeServer(NeServer neServer){
		return neServerDao.updateNeServer(neServer);
	}

	/**
	 * 删除网元
	 * @param serverIds
	 * @return 失败列表
	 */
	public String deleteNeServer(String serverIds){
		StringBuffer failServerIds = new StringBuffer();
		String[] ids = serverIds.split(",");
		for(String serverId : ids){
			try {
				neServerDao.deleteNeServer(Long.parseLong(serverId));
			} catch (Exception e) {
				e.printStackTrace();
				failServerIds.append(serverId+",");
			}
		}
		return failServerIds.toString();
	}
}
