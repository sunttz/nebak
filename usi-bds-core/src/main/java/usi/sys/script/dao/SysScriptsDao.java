package usi.sys.script.dao;

import java.util.List;
import usi.sys.script.dto.SysScriptsDto;
import usi.sys.dto.PageObj;

public interface SysScriptsDao {
	/**
	 * 分页查询执行脚本
	 */
	public List<SysScriptsDto> getScrConfig(String scriptCode, String scriptName, String scriptContent,Integer module,Integer scriptType,Integer isDeploy,String mockInparam,String memo,PageObj pageObj);
	
}
