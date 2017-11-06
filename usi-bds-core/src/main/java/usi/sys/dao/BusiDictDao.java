package usi.sys.dao;

import java.util.List;

import usi.sys.dto.DictDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.entity.SysScene;
/**
 * 
 * @author long.ming
 * 创建时间：2014-3-26 下午2:56:48
 */
public interface BusiDictDao {

	//业务场景查询
	public List<SysScene> querySysSceneByPage(PageObj pageObj,String busiSceneCode,String busiSceneName);
	//新增时查询业务场景
	public List<SysScene> querySysSceneData(String busiSceneCode);
	//业务场景新增
	public void insertSysScene(SysScene sysScene);
	//业务场景修改
	public int updateSysScene(SysScene sysScene);
	//业务场景逻辑删除
	public int deleteSysScene(SysScene sysScene);
	//业务字典查询
	public List<BusiDict> queryBusiDictByPage(String busiSceneCode);
	//新增时业务字典查询校验
	public List<BusiDict> queryBusiDict(String busiSceneCode,String dicCode);
	//业务字典新增
	public void insertBusiDict(BusiDict busiDict);
	//业务字典修改
	public int updateBusiDict(BusiDict busiDict);
	//业务字典逻辑删除
	public int deleteBusiDict(BusiDict busiDict);
	//业务字典查询使用
	public List<DictDto> getDictionaryByCode(String busiSceneCode);
	
	/**
	 * 根据多个场景编码查询字典
	 * @param busiSceneCodes
	 * @return
	 */
	public List<DictDto> getDictionaryByCodes(String[] busiSceneCodes);
}
