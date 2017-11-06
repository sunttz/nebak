package usi.sys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usi.sys.dao.BusiDictDao;
import usi.sys.dto.DictDto;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.entity.SysScene;
import usi.sys.util.ConstantUtil;
/**
 * 
 * @author long.ming
 * 创建时间：2014-3-26 下午2:56:48
 */
@Service
public class BusiDictService {
	@Resource
	private  BusiDictDao dao;
	//分页查询业务场景
	public Map<String, Object> querySysSceneByPage(PageObj pageObj,String busiSceneCode,String busiSceneName){
		Map<String, Object> map = new HashMap<String, Object>();
		List<SysScene> sysScene = dao.querySysSceneByPage(pageObj,busiSceneCode,busiSceneName);
		map.put("total", pageObj.getTotal());
		map.put("rows", sysScene);
		return map;
	}
	//新增时查询业务场景
	public Map<String, Object> querySysSceneData(String busiSceneCode){
		Map<String, Object> map = new HashMap<String, Object>();
		List<SysScene> sysScene = dao.querySysSceneData(busiSceneCode);
		map.put("total", sysScene.size());
		map.put("rows", sysScene);
		return map;
	}
	//增加业务场景
	@Transactional(rollbackFor=Exception.class)
	public void insertSysScene(SysScene sysScene){
		dao.insertSysScene(sysScene);
	}
	//更新业务场景
	@Transactional(rollbackFor=Exception.class)
	public void updateSysScene(SysScene sysScene){
		dao.updateSysScene(sysScene);
	}
	//删除业务场景
	@Transactional(rollbackFor=Exception.class)
	public void deleteSysScene(SysScene sysScene){
		dao.deleteSysScene(sysScene);
	}
	//查询业务字典
	public Map<String, Object> queryBusiDictByPage(String busiSceneCode){
		Map<String, Object> map = new HashMap<String, Object>();
		List<BusiDict> busiDic = dao.queryBusiDictByPage(busiSceneCode);
		map.put("total", busiDic.size());
		map.put("rows", busiDic);
		return map;
	}
	//新增时查询业务字典校验
	public Map<String, Object> queryBusiDict(String busiSceneCode,String dicCode){
		Map<String, Object> map = new HashMap<String, Object>();
		List<BusiDict> busiDic = dao.queryBusiDict(busiSceneCode,dicCode);
		map.put("total", busiDic.size());
		map.put("rows", busiDic);
		return map;
	}
	//增加数据字典
	@Transactional(rollbackFor=Exception.class)
	@CacheEvict(value=ConstantUtil.CACHE_NAME_DICT, key = "#busiDict.getBusiSceneCode()")
	public void insertBusiDict(BusiDict busiDict){
		dao.insertBusiDict(busiDict);
	}
	//更新数据字典
	@Transactional(rollbackFor=Exception.class)
	@CacheEvict(value=ConstantUtil.CACHE_NAME_DICT, key = "#busiDict.getBusiSceneCode()")
	public void updateBusiDict(BusiDict busiDict){
		dao.updateBusiDict(busiDict);
	}
	//删除业务字典
	@Transactional(rollbackFor=Exception.class)
	@CacheEvict(value=ConstantUtil.CACHE_NAME_DICT, key = "#busiDict.getBusiSceneCode()")
	public void deleteBusiDict(BusiDict busiDict){
		dao.deleteBusiDict(busiDict);
	}
	
	/**
	 * 查询字典
	 * @param busiSceneCode
	 * @return
	 */
	@Cacheable(value=ConstantUtil.CACHE_NAME_DICT, key = "#busiSceneCode")
	public List<DictDto> getDictByCode(String busiSceneCode) {
		List<DictDto> list = dao.getDictionaryByCode(busiSceneCode);
		return list;
	}
	
	/**
	 * 根据多个场景编码查询字典
	 * @param busiSceneCodes
	 * @return
	 */
	public Map<String,List<DictDto>> getDictByCodes(String[] busiSceneCodes) {
		Map<String,List<DictDto>> map = new HashMap<String,List<DictDto>>();
		List<DictDto> list = dao.getDictionaryByCodes(busiSceneCodes);
		for(String code : busiSceneCodes){
			List<DictDto> diclist = new ArrayList<DictDto>();
			for(DictDto dict : list){
				if(dict.getBusiSceneCode().equals(code)){
					diclist.add(dict);
				}
			}
			map.put(code, diclist);
		}
		return map;
	}
}
