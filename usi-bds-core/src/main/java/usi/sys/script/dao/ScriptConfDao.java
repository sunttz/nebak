package usi.sys.script.dao;

import java.util.List;

import usi.sys.script.dto.ScriptDto;

/**
 * @Description 
 * @author zhang.dechang
 * @date 2015年8月20日 上午10:20:11
 */
public interface ScriptConfDao {
	/**
	 * 新增脚本
	 * @param sriptDto
	 */
	public void insertScriptFromExternal(ScriptDto scriptDto);
	
	/**
	 * 更新脚本(脚本内容、脚本类型)
	 * @param sriptDto
	 */
	public void updateScriptFromExternal(ScriptDto scriptDto);
	
	/**
	 * 逻辑删除脚本
	 * @param scriptCode 脚本编码
	 */
	public void removeScriptFromExternal(String scriptCode);
	
	/**
	 * 根据脚本编码查询脚本配置信息
	 * @return
	 */
	public List<ScriptDto> getScriptDtoByscriptCode(String scriptCode);
	
	/**
	 * 查询所有的脚本
	 * @return
	 */
	public List<ScriptDto> getAllScript();
}
