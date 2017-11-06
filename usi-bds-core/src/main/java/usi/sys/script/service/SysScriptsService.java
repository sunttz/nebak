package usi.sys.script.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import usi.sys.dto.PageObj;
import usi.sys.script.dao.SysScriptsDao;

@Service
public class SysScriptsService {

	@Resource
	private  SysScriptsDao sysScriptsDao;

	/**
	 * 分页查询执行脚本配置
	 * @param scriptCode 脚本编码
	 * @param scriptName 脚本名称
	 * @param scriptContent  脚本内容	
	 * @param module  所属模块
	 * @param scriptType  脚本类型
	 * @param isDeploy  发布状态
	 * @param mockInparam  模拟入参
	 * @param memo  备注
	 * @return
	 */
	public Map<String, Object> getScrConfig(String scriptCode, String scriptName, String scriptContent,Integer module,Integer scriptType,Integer isDeploy,String mockInparam,String memo,PageObj pageObj) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("rows", sysScriptsDao.getScrConfig(scriptCode,scriptName,scriptContent,module,scriptType,isDeploy,mockInparam,memo,pageObj));
		map.put("total", pageObj.getTotal());
		return map;
	}
	
}
