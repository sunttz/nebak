package usi.biz.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;
import usi.biz.dao.AutoLogDao;
import usi.biz.dao.BakResultDao;
import usi.biz.dao.NeServerDao;
import usi.biz.entity.AutoLog;
import usi.biz.entity.AutoLogDto;
import usi.biz.entity.BakResult;
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
	@Resource
	private BakResultDao bakResultDao;
	
	private static String rootName=ConfigUtil.getValue("download.file.path");
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");

	// 备份ftp服务器配置
	private static String ftpflag=ConfigUtil.getValue("bak.ftp.flag");
	private static String ftpIp=ConfigUtil.getValue("bak.ftp.ip");
	private static String ftpUsername=ConfigUtil.getValue("bak.ftp.username");
	private static String ftpPassword=ConfigUtil.getValue("bak.ftp.password");

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
	public List<AutoLogDto> getAutoResult(String dateTime,PageObj pageObj){
		return neServerDao.getAutoResult(dateTime,pageObj);
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
			String bakType = neserver.getBakType(); // 备份类型
			// 被动取(去指定ftp主机下载)
			if("0".equals(bakType)){
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
			// 主动推(检查指定路径是否有文件)
			else if("1".equals(bakType)){
				String bakDir = getBakDir(neserver.getBakPath(), neserver.getOrgName(), neserver.getDeviceType(), neserver.getDeviceName());
				// 推送到ftp
				if("true".equals(ftpflag)){
					try{
						boolean isExits = FtpUtils.dirExits(bakDir, ftpIp, port, ftpUsername, ftpPassword, activeTime);
						if(!isExits){
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
				// 推送到本地
				else{
					try {
						File file = new File(bakDir);
						if(!file.exists() || file.listFiles().length == 0){
							if(result.equals("")){
								result=serverIds[i];
							}else{
								result+=","+serverIds[i];
							}
                        }
					} catch (Exception e) {
						e.printStackTrace();
						if(result.equals("")){
							result=serverIds[i];
						}else{
							result+=","+serverIds[i];
						}
					}
				}
			}else{
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
		// 备份结果（1成功0失败）
		int bakFlag;
		// 备份成功数
		long succNum = 0l;
		// 备份失败数
		long failNum = 0l;

		//先删除日志，再进行备份
		bakResultDao.deleteBakResultByTime();
		// 删除备份路径下空文件夹
		deleteEmptyFile();
		//第一步获取单个网元信息
		String[] serverIds=ids.split(",");
		for(int i=0;i<serverIds.length;i++){
			bakFlag = 1;
			List<NeServer> list = neServerDao.getNeServerById(Long.parseLong(serverIds[i]));
			NeServer neserver=list.get(0);
			System.out.println("========网元设备【"+neserver.getDeviceName()+"】备份开始=======");
			String bakType = neserver.getBakType(); // 备份类型
			// 被动取(去指定ftp主机下载)
			if("0".equals(bakType)){
				try {
					// 删除该网元本地过期文件
					deleteLocalExpireFile("",neserver.getOrgName(),neserver.getDeviceType(),neserver.getDeviceName(),neserver.getSaveDay());
				} catch (Exception e) {
					e.printStackTrace();
				}
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
					System.out.println("==============ftp下载flag(返回标志位):"+flag);
					if(!flag){
						if(result.equals("")){
							result=serverIds[i];
						}else{
							result+=","+serverIds[i];
						}
					}
					bakFlag=flag==true?1:0;
				}catch(Exception e){
					e.printStackTrace();
					if(result.equals("")){
						result=serverIds[i];
					}else{
						result+=","+serverIds[i];
					}
					bakFlag = 0;
				}
			}
			// 主动推(检查指定路径是否有文件)
			else if("1".equals(bakType)){
				String bakDir = getBakDir(neserver.getBakPath(), neserver.getOrgName(), neserver.getDeviceType(), neserver.getDeviceName());
				// 推送到ftp
				if("true".equals(ftpflag)){
					try{
						// 删除ftp过期文件
						deleteFtpExpireFile(neserver.getBakPath(),neserver.getOrgName(),neserver.getDeviceType(),neserver.getDeviceName(),neserver.getSaveDay());
						boolean isExits = FtpUtils.dirExits(bakDir, ftpIp, port, ftpUsername, ftpPassword, activeTime);
						if(!isExits){
							if(result.equals("")){
								result=serverIds[i];
							}else{
								result+=","+serverIds[i];
							}
						}
						bakFlag=isExits==true?1:0;
					}catch(Exception e){
						e.printStackTrace();
						if(result.equals("")){
							result=serverIds[i];
						}else{
							result+=","+serverIds[i];
						}
						bakFlag = 0;
					}
				}
				// 推送到本地
				else{
					try {
						// 删除本地过期文件
						deleteLocalExpireFile(neserver.getBakPath(),neserver.getOrgName(),neserver.getDeviceType(),neserver.getDeviceName(),neserver.getSaveDay());
						// 开始检查备份
						File file = new File(bakDir);
						if(!file.exists() || file.listFiles().length == 0){
							if(result.equals("")){
								result=serverIds[i];
							}else{
								result+=","+serverIds[i];
							}
							bakFlag = 0;
						}
					} catch (Exception e) {
						e.printStackTrace();
						if(result.equals("")){
							result=serverIds[i];
						}else{
							result+=","+serverIds[i];
						}
						bakFlag = 0;
					}
				}
			}else{
				if(result.equals("")){
					result=serverIds[i];
				}else{
					result+=","+serverIds[i];
				}
				bakFlag = 0;
			}
			this.addAutoLog(Long.parseLong(serverIds[i]),bakFlag);
			succNum += bakFlag;
			failNum += bakFlag==0?1:0;
			System.out.println("========网元设备【"+neserver.getDeviceName()+"】备份结束=======");
		}
		// 保存备份结果
		BakResult bakResult = new BakResult(succNum,failNum);
		bakResultDao.saveBakResult(bakResult);
		System.out.println("==============result(错误结果):"+result);
		System.out.println("==============备份成功数:"+succNum);
		System.out.println("==============备份失败数:"+failNum);
		return result;
	}

	/**
	 * 遍历所有一级文件夹，对于空文件，删除
	 */
	private void deleteEmptyFile(){
		File rootFile = new File(rootName);
		File[] files = rootFile.listFiles();
		if(files != null){
			for (File f : files) {
				if(f.isDirectory() && f.listFiles().length == 0){
					f.delete();
				}
			}
		}
	}

	/**
	 * 删除本地超过保存天数的备份文件
	 * @param bakPath 备份路径
	 * @param orgName
	 * @param deviceType
	 * @param deviceName
	 * @param saveDay
	 */
	private void deleteLocalExpireFile(String bakPath,String orgName,String deviceType,String deviceName, long saveDay) throws Exception {
		String rootName = ""; // 被动取的根目录为rootName,主动推送的为配置项
		if(StringUtils.isNotEmpty(bakPath)){
			rootName = bakPath;
		}else{
			rootName = this.rootName;
		}
		// 1.遍历所有一级文件夹，取出地域和网元类型匹配且日期在当前设备备份天数之前的文件夹名放到expireFolder集合中
		List<String> expireFolders = new ArrayList<>();
		String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);//获取地市首字母
		String matchStr = englishOrgName + "_" + deviceType; // 匹配字符
		Date expireDate = getExpireDate(saveDay); // 过期日期
		File rootFile = new File(rootName);
		File[] files = rootFile.listFiles();
		if(files != null){
			for(File f : files){
				if(f.isDirectory()){
					String fileName = f.getName();
					if(fileName.indexOf(matchStr) > -1){
						String[] splitName = fileName.split("_");
						if(splitName.length == 3){
							String fileDateStr = splitName[2];
							Date fileDate = sdf.parse(fileDateStr);
							if(fileDate.getTime() < expireDate.getTime()){
								expireFolders.add(fileName);
							}
						}
					}
				}
			}
		}
		// 2.遍历expireFolder集合文件夹，判断当天该网元备份文件夹是否存在，存在则删除
		String matchStr2 = matchStr + "_" + deviceName;
		for (String folder : expireFolders) {
			File file = new File(rootName + File.separator + folder);
			File[] listFiles = file.listFiles();
			for (File f : listFiles) {
				String secondFileName = f.getName();
				if(secondFileName.indexOf(matchStr2) > -1){
					deleteFile(f);
					System.out.printf("删除本地备份过期文件夹%s%n", secondFileName);
					break;
				}
			}
		}
	}

	/**
	 * 删除ftp超过保存天数的备份文件
	 * @param bakPath 备份路径
	 * @param orgName
	 * @param deviceType
	 * @param deviceName
	 * @param saveDay
	 * @throws Exception
	 */
	public void deleteFtpExpireFile(String bakPath,String orgName,String deviceType,String deviceName, long saveDay) throws Exception {
		FtpUtils.deleteEmptyFile(bakPath,ftpIp,21,ftpUsername,ftpPassword,3000); // 删除空文件
		List<String> expireFolders = new ArrayList<>();
		String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);//获取地市首字母
		String matchStr = englishOrgName + "_" + deviceType; // 匹配字符
		Date expireDate = getExpireDate(saveDay); // 过期日期
		FTPClient ftpClient = new FTPClient();
		ftpClient = FtpUtils.getFTPClient(ftpIp, 21, ftpUsername, ftpPassword,3000);
		ftpClient.setBufferSize(1024);
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		final boolean changeFlag = ftpClient.changeWorkingDirectory(bakPath + File.separator);
		if(changeFlag){
			// 1.遍历所有一级文件夹，取出地域和网元类型匹配且日期在当前设备备份天数之前的文件夹名放到expireFolder集合中
			FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile ftpFile : ftpFiles) {
				if(ftpFile.isDirectory()){
					String fileName = ftpFile.getName();
					if(fileName.indexOf(matchStr) > -1){
						String[] splitName = fileName.split("_");
						if(splitName.length == 3){
							String fileDateStr = splitName[2];
							Date fileDate = sdf.parse(fileDateStr);
							if(fileDate.getTime() < expireDate.getTime()){
								expireFolders.add(fileName);
							}
						}
					}
				}
			}
			// 2.遍历expireFolder集合文件夹，判断当天该网元备份文件夹是否存在，存在则删除
			String matchStr2 = matchStr + "_" + deviceName;
			for (String folder : expireFolders) {
				boolean changeFlag2 = ftpClient.changeWorkingDirectory(bakPath + File.separator + folder);
				if(changeFlag2){
					FTPFile[] ftpFiles2 = ftpClient.listFiles();
					for (FTPFile f : ftpFiles2) {
						String secondFileName = f.getName();
						if(secondFileName.indexOf(matchStr2) > -1){
							String filePath = bakPath + File.separator + folder + File.separator + secondFileName + File.separator;
							boolean deleteFlag = FtpUtils.iterateDelete(ftpClient, filePath);
							System.out.printf(String.format("删除FTP备份过期文件夹%s,结果%s%n", secondFileName, deleteFlag));
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 获取过期日期
	 * @param day
	 * @return
	 */
	private Date getExpireDate(long day) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -(int) day);
		date = calendar.getTime();
		return date;
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
	 * 返回主动推送备份文件夹路径
	 * @param rootName
	 * @param orgName
	 * @param deviceType
	 * @param deviceName
	 * @return
	 */
	public String getBakDir(String rootName, String orgName,String deviceType,String deviceName){
		//获取地市首字母
		String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);
		//获取当前年月日
		String date=getDate();
		String firstFolderName=rootName+File.separator+englishOrgName+"_"+deviceType+"_"+date;
		String secondFolderName=firstFolderName+File.separator+englishOrgName+"_"+deviceType+"_"+deviceName+"_"+date;
		System.out.println("===============备份文件夹路径:"+secondFolderName);
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
	 * 递归删除文件及文件夹
	 * @param file
	 */
	public void deleteFile(File file) {
		File[] filearray = file.listFiles();
		if (filearray != null) {
			for (File f : filearray) {
				if (f.isDirectory()) {
					deleteFile(f);
				} else {
					f.delete();
				}
			}
			file.delete();
		}
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

	public List<AutoLogDto> getFailResult(String dateTime,PageObj pageObj){
		return neServerDao.getFailResult(dateTime,pageObj);
	}
}
