package usi.sys.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import usi.sys.dto.AuthInfo;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.entity.SysScene;
import usi.sys.service.BusiDictService;
import usi.sys.service.SysOptLogService;
import usi.sys.sync.util.CacheSyncService;
import usi.sys.sync.util.NodeSyncService;
import usi.sys.util.ConfigUtil;
import usi.sys.util.ConstantUtil;
import usi.sys.util.IpAddressUtil;
/**
 * 
 * @author long.ming
 * 创建时间：2014-3-26 下午2:56:48
 */
@Controller
@RequestMapping("/BusiDictControl")
public class BusiDictController {

	@Resource
	BusiDictService busiDictService;
	@Resource
	SysOptLogService sysOptLogService;
	@Resource
	NodeSyncService nodeSyncService;
	@Resource
	CacheSyncService cacheSyncService;
	
	/**
	 * 跳转到元素配置页面
	 * @return
	 */
	@RequestMapping(value = "/toMenu.do", method = RequestMethod.GET)
	public String getMain(Model model) {
		return "system/busiDictControl";
	}
	/**
	 * 新增时查询业务场景
	 */
	@RequestMapping(value = "/querySysSceneDataControl.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> querySysSceneData(String busiSceneCode){
		String sceneCode = null;
		if(StringUtils.hasText(busiSceneCode)){
			sceneCode = busiSceneCode;
		}
		Map<String, Object> modelMap =  busiDictService.querySysSceneData(sceneCode);
		return modelMap;
	}
	/**
	 * 查询所有业务场景
	 */
	@RequestMapping(value = "/querySysSceneControl.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> querySysSceneByPage(PageObj pageObj,String busiSceneCode,String busiSceneName){
		String sceneCode = null;
		String sceneName = null;
		if(StringUtils.hasText(busiSceneCode)){
			sceneCode = busiSceneCode;
		}
		if(StringUtils.hasText(busiSceneName)){
			sceneName = busiSceneName;
		}
		Map<String, Object> modelMap =  busiDictService.querySysSceneByPage(pageObj,sceneCode,sceneName);
		return modelMap;
	}
	/**
	 * 增加业务场景信息
	 */
	@RequestMapping(value = "/insertSysSceneControl.do", method = RequestMethod.POST)
	public void insertSysScene(HttpServletRequest request,HttpSession session, SysScene sysScene,PrintWriter pw){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			sysScene.setCreateStaff(userId);
			sysScene.setStatus(1);
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.insertSysScene(IpAddressUtil.getReqIp(request),userId,sysScene);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			busiDictService.insertSysScene(sysScene);
			pw.print("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.print("fail");
		}
	}
	/**
	 * 更新业务场景信息
	 */
	@RequestMapping(value = "/updateSysSceneControl.do", method = RequestMethod.POST)
	public void updateSysScene(HttpServletRequest request,HttpSession session,PrintWriter pw,SysScene sysScene){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			SysScene beforeUpdate = null;
			if(ConstantUtil.WRITELOG){
				beforeUpdate = sysOptLogService.getSysSceneByCode(sysScene.getBusiSceneCode());
			}
			busiDictService.updateSysScene(sysScene);
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.updateSysScene(IpAddressUtil.getReqIp(request),userId,beforeUpdate,sysScene);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.print("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.print("fail");
		}
	}
	/**
	 * 查询所有业务字典
	 */
	@RequestMapping(value = "/queryPageBusiDictControl.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryBusiDictByPage(String busiSceneCode){
		String sceneCode = null;
		if(StringUtils.hasText(busiSceneCode)){
			sceneCode = busiSceneCode;
		}
		Map<String, Object> modelMap =  busiDictService.queryBusiDictByPage(sceneCode);
		return modelMap;
	}
	/**
	 * 新增时查询业务字典
	 */
	@RequestMapping(value = "/queryAddBusiDictControl.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryBusiDict(String busiSceneCode,String dicCode){
		String sceneCode = null;
		if(StringUtils.hasText(busiSceneCode)){
			sceneCode = busiSceneCode;
		}
		Map<String, Object> modelMap =  busiDictService.queryBusiDict(sceneCode,dicCode);
		return modelMap;
	}
	/**
	 * 逻辑删除业务场景信息
	 */
	@RequestMapping(value = "/deleteSysSceneControl.do", method = RequestMethod.POST)
	public void deleteSysScene(HttpServletRequest request,HttpSession session,PrintWriter pw,SysScene sysScene){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			SysScene beforeDel = null;
			if(ConstantUtil.WRITELOG){
				beforeDel = sysOptLogService.getSysSceneByCode(sysScene.getBusiSceneCode());
			}
			busiDictService.deleteSysScene(sysScene);
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.deleteSysScene(IpAddressUtil.getReqIp(request),userId,beforeDel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.write("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.write("fail");
		}
	}
	/**
	 * 增加业务字典信息
	 */
	@RequestMapping(value = "/insertBusiDictControl.do", method = RequestMethod.POST)
	public void insertBusiDict(HttpServletRequest request,HttpSession session,PrintWriter pw,BusiDict busiDict){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			busiDict.setCreateStaff(userId);
			busiDict.setStatus(1);
			
			busiDictService.insertBusiDict(busiDict);
			// 清除集群中其他节点的缓存
			if("false".equals(ConfigUtil.getValue("singlePoint"))){
				String uri = "/BusiDictControl/evictDictCache.notice";
				Map<String,String> paramsMap = new HashMap<String, String>();
				paramsMap.put("busiSceneCode", busiDict.getBusiSceneCode());
				nodeSyncService.invokeHttpclient4Sync(uri, paramsMap);
			}
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.insertBusiDict(IpAddressUtil.getReqIp(request),userId,busiDict);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.write("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.write("fail");
		}
	}
	/**
	 * 更新业务字典信息
	 */
	@RequestMapping(value = "/updateBusiDictControl.do", method = RequestMethod.POST)
	public void updateBusiDict(HttpServletRequest request,HttpSession session,PrintWriter pw,BusiDict busiDict){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			BusiDict beforeUpdate = null;
			if(ConstantUtil.WRITELOG){
				beforeUpdate = sysOptLogService.getBusiDictById(busiDict.getDicId());
			}
			busiDictService.updateBusiDict(busiDict);
			// 清除集群中其他节点的缓存
			if("false".equals(ConfigUtil.getValue("singlePoint"))){
				String uri = "/BusiDictControl/evictDictCache.notice";
				Map<String,String> paramsMap = new HashMap<String, String>();
				paramsMap.put("busiSceneCode", busiDict.getBusiSceneCode());
				nodeSyncService.invokeHttpclient4Sync(uri, paramsMap);
			}
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.updateBusiDict(IpAddressUtil.getReqIp(request),userId,beforeUpdate,busiDict);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.write("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.write("fail");
		}
	}
	/**
	 * 逻辑删除业务字典信息
	 */
	@RequestMapping(value = "/deleteBusiDictControl.do", method = RequestMethod.POST)
	public void deleteBusiDict(HttpServletRequest request,HttpSession session,PrintWriter pw,BusiDict busiDict){
		try{
			AuthInfo auth = (AuthInfo)session.getAttribute(ConstantUtil.AUTH_INFO);
			String userId = auth.getUserId();
			BusiDict beforeDel = null;
			//是否记录操作日志
			if(ConstantUtil.WRITELOG){
				beforeDel = sysOptLogService.getBusiDictById(busiDict.getDicId());
			}
			busiDictService.deleteBusiDict(busiDict);
			// 清除集群中其他节点的缓存
			if("false".equals(ConfigUtil.getValue("singlePoint"))){
				String uri = "/BusiDictControl/evictDictCache.notice";
				Map<String,String> paramsMap = new HashMap<String, String>();
				paramsMap.put("busiSceneCode", busiDict.getBusiSceneCode());
				nodeSyncService.invokeHttpclient4Sync(uri, paramsMap);
			}
			
			if(ConstantUtil.WRITELOG){
				try {
					sysOptLogService.deleteBusiDict(IpAddressUtil.getReqIp(request),userId,beforeDel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			pw.write("success");
		}catch(Exception e){
			e.printStackTrace();
			pw.write("fail");
		}
	}
	
	/**
	 * 清除指定的字典缓存
	 * 用于节点同步时接受其他节点的请求
	 * @param busiSceneCode
	 */
	@RequestMapping(value = "/evictDictCache.notice", method = RequestMethod.POST)
	public void evictDictCache(String busiSceneCode){
		cacheSyncService.evictCache(ConstantUtil.CACHE_NAME_DICT, busiSceneCode);
	}
}
