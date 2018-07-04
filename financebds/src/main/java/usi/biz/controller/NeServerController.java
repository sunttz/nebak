package usi.biz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import usi.biz.entity.AutoLogDto;
import usi.biz.entity.BakResult;
import usi.biz.entity.NeServer;
import usi.biz.service.BakResultService;
import usi.biz.service.NeServerService;
import usi.biz.util.DiskInfoUtil;
import usi.sys.dto.AuthInfo;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.util.ConfigUtil;
import usi.sys.util.ConstantUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/netElement")
public class NeServerController {
	
	@Resource
	private NeServerService neServerService;

	@Resource
	BakResultService bakResultService;
	
	private static String rootName=ConfigUtil.getValue("download.file.path");
	
	/**
	 * 网元serve页面
	 * @return
	 */
	@RequestMapping(value = "/main.do", method = RequestMethod.GET)
	public String getMain(Model model){
		Map<Object,String> map=DiskInfoUtil.DiskInfo();
		String diskinfo="当前备份服务器已用空间容量："+map.get("usedSpace").toString()
					   +",空闲空间容量："+map.get("freeSpace").toString()
					   +",请注意及时做备份管理节省空间!";
		model.addAttribute("diskinfo", diskinfo);
		return "ne_server/ne_server";
	}
	
	/**
	 * 获取所有orgName
	 * @return
	 */
	@RequestMapping(value = "/getAllOrg.do", method = RequestMethod.POST)
	@ResponseBody
	public List<NeServer> getAllOrg(HttpSession session){
		AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
		Long orgId=auth.getOrgId();
		String orgName=auth.getOrgName();
		List<NeServer> result = new ArrayList<NeServer>();
		List<NeServer> areaList= new ArrayList<NeServer>();
		//如果是合肥或者省公司机构只能查询本地分公司的
		if(orgId==1L||orgId == 2L){
			areaList = neServerService.getAllOrg();
			NeServer neServer = new NeServer();
			neServer.setOrgId(-1L);
			neServer.setOrgName("全部");
			result.add(neServer);
			result.addAll(areaList);
		}else{//如果不是，则只传本地分公司的
			NeServer neServer = new NeServer();
			neServer.setOrgId(orgId);
			neServer.setOrgName(orgName);
			result.add(neServer);
		}
		return result;
	}

	/**
	 * 获取所有orgName(新增网元时使用)
	 * @return
	 */
	@RequestMapping(value = "/getAllOrg2.do", method = RequestMethod.POST)
	@ResponseBody
	public List<NeServer> getAllOrg2(HttpSession session){
		List<NeServer> result = neServerService.getAllOrg2();
		return result;
	}

	/**
	 * 获取所有厂家名称
	 * @return
	 */
	@RequestMapping(value = "/getAllFirms.do")
	@ResponseBody
	public List<BusiDict> getAllFirms(){
		List<BusiDict> result = neServerService.getAllFirms();
		return result;
	}

	/**
	 * 获取所有网元列表
	 * @return
	 */
	@RequestMapping(value = "/getPageAllNE.do", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String,Object> getPageAllNE(PageObj getAllJob, HttpServletRequest request, Long orgId, String deviceType){
		String deviceName = request.getParameter("deviceName");
		String bakType = request.getParameter("bakType");
		String saveType = request.getParameter("saveType");
		String saveDay = request.getParameter("saveDay");
		return neServerService.getPageAllNE(getAllJob,orgId,deviceType,deviceName,bakType,saveType,saveDay);
	}
	
	/**
	 * 网元备份
	 * @return
	 */
	@RequestMapping(value = "/bakNow.do", method = RequestMethod.POST)
	@ResponseBody
	public  String  bakNow(String ids){
		return neServerService.bakNow(ids);
	}
	
	
	/**
	 * 备份服务器的所有文件夹页面
	 * @return
	 */
	@RequestMapping(value = "/resultmain.do", method = RequestMethod.GET)
	public String getResultMain(Model model){
		model.addAttribute("filePath", rootName);
		return "ne_server/result";
	}
	/**
	 * 获取所有网元备份后列表
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getResult.do", method = RequestMethod.POST)
	@ResponseBody
	public  List<Map<String,Object>> getResult(HttpSession session,String orgName,String dateTime,String filePath) throws Exception{
			orgName=orgName.equals("全部")?"":orgName;
			return neServerService.getResult(orgName,dateTime,filePath);
	}
	
	/**
	 * 自动备份初始化页面
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/autoResult.do", method = RequestMethod.GET)
	public String autoResult(Model model){
		return "ne_server/autoResultInit";
	}
	
	/**
	 * 获取所有网元备份后列表
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getAutoResult.do", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String,Object> getAutoResult(PageObj pageObj,HttpSession session,String dateTime) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		if(dateTime==null ||dateTime.equals("")){
			Calendar now = Calendar.getInstance();
			String year=String.valueOf(now.get(Calendar.YEAR));
			String month= String.valueOf(now.get(Calendar.MONTH) + 1);
			String day=String.valueOf(now.get(Calendar.DAY_OF_MONTH));
			month=month.length()<2?'0'+month:month;
			day=day.length()<2?'0'+day:day;
			dateTime=year+month+day;
		}
		List<AutoLogDto> autoResult = neServerService.getAutoResult(dateTime, pageObj);
		map.put("total", pageObj.getTotal());
		map.put("rows", autoResult);
		return map;
	}
	
	/**
	 * 实现网元文件夹的下载功能
	 * @param souceFileName
	 * @param response
	 */
	@RequestMapping(value="uploadZipFile",method= RequestMethod.GET)
	public void uploadZipFile(String souceFileName,HttpServletResponse response){
		File souceFile = new File(souceFileName);
		String zipFilePath = souceFileName;
		// 文件夹需压缩，文件不压缩
		boolean isDirectory = souceFile.isDirectory();
		if(isDirectory){
			zipFilePath += ".zip";
		}
		String fileName=zipFilePath.substring(zipFilePath.lastIndexOf("/")+1);
		System.out.println(zipFilePath);
		// System.out.println(fileName);
		FileInputStream fis =null;
		OutputStream out = null;
		try {
			if(isDirectory){
				zip(souceFile, zipFilePath);
			}
			out = new BufferedOutputStream(response.getOutputStream());
			File file = new File(zipFilePath);
	        // response.setContentType("application/zip ");
	        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
			response.addHeader("Content-Length", "" + file.length());
			response.setContentType("application/octet-stream");
			fis = new FileInputStream(file);
			// byte[] b = new byte[fis.available()];
			byte[] b = new byte[1024 * 1024 * 10];
			int i = -1;
			while ((i = fis.read(b)) != -1) {
				out.write(b,0, i);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File file1 = new File(zipFilePath);
			if(isDirectory && file1.isFile() && file1.exists()){
				file1.delete();
			}
		}
		
	}
	
	   private void zip(File souceFile, String destFileName) throws IOException {  
	        FileOutputStream fileOut = null;  
	        try {  
	            fileOut = new FileOutputStream(destFileName);  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        }  
	        ZipOutputStream out = new ZipOutputStream(fileOut);
	        zip(souceFile, out, "");  
	        out.close();  
	    }  
	 
    //实现文件和文件夹的打包
    public void zip(File souceFile, ZipOutputStream out, String base)  
            throws IOException {  	
    	  if (souceFile.isDirectory()) {  
              File[] files = souceFile.listFiles();  
              out.putNextEntry(new ZipEntry(base + "/"));  
              base = base.length() == 0 ? "" : base + "/";  
              for (File file : files) {  
                  zip(file, out, base + file.getName());  
              }  
          } else {  
              if (base.length() > 0) {  
                  out.putNextEntry(new ZipEntry(base));  
              } else {  
                  out.putNextEntry(new ZipEntry(souceFile.getName()));  
              } 
              FileInputStream in = new FileInputStream(souceFile);  
              
              int b;  
              byte[] by = new byte[1024];
              while ((b = in.read(by)) != -1) {  
                  out.write(by, 0, b);  
              }  
              in.close();  
              out.flush();
          } 
    }

	/**
	 * 网元serve配置页面
	 * @return
	 */
	@RequestMapping(value = "/neServerConfig.do", method = RequestMethod.GET)
	public String neServerConfig(Model model){
		return "ne_server/neServerConfig";
	}

	/**
	 * 网元配置-保存
	 * @param neServer
	 * @return
	 */
	@RequestMapping(value = "/saveNeserver.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean addNeserver(NeServer neServer){
		boolean flag = false;
		try {
			int result = -1;
			Long serverId = neServer.getServerId();
			if(serverId != null){
				result = neServerService.updateNeServer(neServer);
			}else{
				result = neServerService.saveNeServer(neServer);
			}
			if(result == 1){
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 网元配置-删除
	 * @param serverIds
	 * @return
	 */
	@RequestMapping(value = "/deleteNeserver.do", method = RequestMethod.POST)
	@ResponseBody
	public String deleteNeserver(String serverIds){
		String failServerIds = neServerService.deleteNeServer(serverIds);
		return failServerIds;
	}

	/**
	 * 自动结果统计
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/autoResultStatistics.do", method = RequestMethod.GET)
	public String autoResultStatistics(Model model){
		return "ne_server/autoResultStatistics";
	}

	/**
	 * 备份失败列表
	 * @param pageObj
	 * @param session
	 * @param dateTime
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getFailResult.do", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String,Object> getFailResult(PageObj pageObj,HttpSession session,String dateTime) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		if(dateTime==null ||dateTime.equals("")){
			Calendar now = Calendar.getInstance();
			String year=String.valueOf(now.get(Calendar.YEAR));
			String month= String.valueOf(now.get(Calendar.MONTH) + 1);
			String day=String.valueOf(now.get(Calendar.DAY_OF_MONTH));
			month=month.length()<2?'0'+month:month;
			day=day.length()<2?'0'+day:day;
			dateTime=year+month+day;

		}
		List<AutoLogDto> failResult = neServerService.getFailResult(dateTime, pageObj);
		map.put("total", pageObj.getTotal());
		map.put("rows", failResult);
		return map;
	}

	/**
	 * 查询指定天备份结果
	 * @param createDate
	 * @return
	 */
	@RequestMapping(value = "/getBakResultByDay.do", method = RequestMethod.POST)
	@ResponseBody
	public BakResult getBakResultByDay(String createDate){
		BakResult bakResult = null;
		try {
			bakResult = bakResultService.queryByTime(createDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bakResult;
	}

	/**
	 * 查询指定时间段备份结果
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/getBakResultByTime.do", method = RequestMethod.POST)
	@ResponseBody
	public List<BakResult> getBakResultByTime(String startDate, String endDate){
		List<BakResult> bakResults = null;
		try {
			bakResults = bakResultService.queryBakResult(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bakResults;
	}

	/**
	 * 备份结果管理-删除
	 * @param delPaths 文件、文件夹路径
	 * @return
	 */
	@RequestMapping(value = "/delFilePaths.do", method = RequestMethod.POST)
	@ResponseBody
	public boolean delFilePaths(String delPaths){
		boolean result = false;
		try {
			String[] delPathsTmp = delPaths.split(",");
			for (String delPath : delPathsTmp) {
                File file = new File(delPath);
                if(file.isDirectory()){
                    neServerService.deleteFile(file);
                }else{
                    file.delete();
                }
            }
            result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
