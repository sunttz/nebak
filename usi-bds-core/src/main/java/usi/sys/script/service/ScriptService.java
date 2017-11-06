package usi.sys.script.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.xquery.XQException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import usi.sys.exception.BdsScriptException;
import usi.sys.script.dao.ScriptConfDao;
import usi.sys.script.dto.ScriptCacheDto;
import usi.sys.script.dto.ScriptDto;
import usi.sys.sync.util.NodeSyncService;
import usi.sys.util.CommonUtil;
import usi.sys.util.ConstantUtil;
import usi.sys.util.GroovyUtil;
import usi.sys.util.JacksonUtil;
import usi.sys.util.XMLUtil;

@Service
public class ScriptService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptService.class);
	
	@Resource
	NodeSyncService nodeSyncService;
	
	@Resource
	CacheManager cacheManager;
	
	@Resource
	ScriptConfDao scriptConfDao;
	
	/**
	 * 通知各节点刷新脚本缓存
	 * @param scriptCode
	 * @throws Exception
	 */
	public void noticeForRefreshScript(String scriptCode) throws Exception{
		List<String> scriptCodeList = new ArrayList<String>();
		scriptCodeList.add(scriptCode);
		noticeForRefreshScript(scriptCodeList);
	}
	
	/**
	 * 通知各节点刷新脚本缓存
	 * @param scriptCodeArray
	 * @throws Exception
	 */
	public void noticeForRefreshScript(String[] scriptCodeArray) throws Exception{
		List<String> scriptCodeList = Arrays.asList(scriptCodeArray);
		noticeForRefreshScript(scriptCodeList);
	}
	
	/**
	 * 通知各节点刷新脚本缓存
	 * @param scriptCodeList
	 * @throws Exception
	 */
	public void noticeForRefreshScript(List<String> scriptCodeList) throws Exception{
		String uri = "/sys/script/publishScript.notice";
		Map<String,String> paramsMap = new HashMap<String, String>();
		paramsMap.put("scriptCodes", JacksonUtil.obj2json(scriptCodeList));
		nodeSyncService.invokeHttpclient4Sync(uri, paramsMap);
	}
	
	/**
	 * 通知各节点刷新全部脚本缓存
	 * @throws Exception
	 */
	public void noticeForRefreshAllScript() throws Exception{
		String uri = "/sys/script/publishAllScript.notice";
		nodeSyncService.invokeHttpclient4Sync(uri, null);
	}
	
	/**
	 * 刷新脚本缓存
	 * @param scriptCodeList
	 */
	public void refreshScript(List<String> scriptCodeList){
		LOGGER.info("开始刷新脚本缓存...");
		for(String scriptCode : scriptCodeList){
			List<ScriptDto> scriptDtoList = scriptConfDao.getScriptDtoByscriptCode(scriptCode);
			if(scriptDtoList.size() > 0){
				parseScriptAndSendToCache(scriptDtoList.get(0));
			} else {
				//从数据库查询不到，认为是删除了
				Cache cache = cacheManager.getCache(ConstantUtil.CACHE_NAME_SCRIPT);
				cache.evict(scriptCode);
			}
		}
		LOGGER.info("刷新脚本缓存完成!!!");
	}
	
	/**
	 * 将指定脚本刷入缓存
	 * 若脚本内容为空，则从缓存中删除
	 * @param scriptDto
	 */
	private ScriptCacheDto parseScriptAndSendToCache(ScriptDto scriptDto){
		Cache cache = cacheManager.getCache(ConstantUtil.CACHE_NAME_SCRIPT);
		//如果脚本内容为空，则从缓存中删除该脚本缓存
		if(!CommonUtil.hasValue(scriptDto.getScriptContent())){
			cache.evict(scriptDto.getScriptCode());
			LOGGER.warn("编码【{}】名称【{}】的脚本内容为空，将从缓存中删除!!!",scriptDto.getScriptCode(),scriptDto.getScriptName());
			return null;
		}
		//在脚本缓存中的数据结构
		ScriptCacheDto scriptCacheDto = new ScriptCacheDto();
		scriptCacheDto.setScriptType(scriptDto.getScriptType());
		//xquery脚本只需要将配置内容直接放入缓存,groovy脚本需要先编译再放入缓存
		if(scriptDto.getScriptType() == ConstantUtil.SCRIPT_TYPE_XQUERY){
			scriptCacheDto.setScriptContent(scriptDto.getScriptContent());
			cache.put(scriptDto.getScriptCode(), scriptCacheDto);
		} else if(scriptDto.getScriptType() == ConstantUtil.SCRIPT_TYPE_GROOVY){
			try {
				Class<?> groovyClass = GroovyUtil.parseClass(scriptDto.getScriptContent());
				scriptCacheDto.setScriptContent(groovyClass);
				cache.put(scriptDto.getScriptCode(), scriptCacheDto);
			} catch (Exception e) {
				//编译失败，不做处理
				e.printStackTrace();
				cache.evict(scriptDto.getScriptCode());
				LOGGER.error("编码【{}】名称【{}】的脚本编译失败，从缓存中删除",scriptDto.getScriptCode(),scriptDto.getScriptName());
				return null;
			}
		}
		return scriptCacheDto;
	}
	
	/**
	 * 执行脚本
	 * 当缓存中没有该脚本时,会尝试从数据库中查找并加载到缓存中
	 * 传到groovy脚本中的变量的标识符为"groovyParam"
	 * @param scriptCode 脚本编码
	 * @param param 参数
	 * @return 脚本解析的返回值，xquery解析后为String类型
	 * @throws BdsScriptException
	 * @throws XQException
	 */
	public Object evalScript(String scriptCode, Object param) throws BdsScriptException, XQException{
		Cache cache = cacheManager.getCache(ConstantUtil.CACHE_NAME_SCRIPT);
		ValueWrapper valueWrapper = cache.get(scriptCode);
		ScriptCacheDto scriptCacheDto = null;
		if(valueWrapper == null){
			List<ScriptDto> scriptDtoList = scriptConfDao.getScriptDtoByscriptCode(scriptCode);
			if(scriptDtoList.size() > 0){
				//尝试编译并放入缓存，若失败返回null
				scriptCacheDto = parseScriptAndSendToCache(scriptDtoList.get(0));
				if(scriptCacheDto == null){
					throw new BdsScriptException("脚本缓存中没有脚本【"+scriptCode+"】【"+scriptDtoList.get(0).getScriptName()+"】,数据库有但是编译失败^..^");
				}
			} else {
				throw new BdsScriptException("脚本缓存中没有脚本【"+scriptCode+"】,且数据库中也没有^..^");
			}
		} else {
			scriptCacheDto = (ScriptCacheDto) valueWrapper.get();
		}
		if(scriptCacheDto.getScriptType() == ConstantUtil.SCRIPT_TYPE_XQUERY){
			if(param == null){
				return XMLUtil.executeXquery((String)scriptCacheDto.getScriptContent());
			} else {
				return XMLUtil.executeXquery((String)param, (String)scriptCacheDto.getScriptContent());
			}
		} else if(scriptCacheDto.getScriptType() == ConstantUtil.SCRIPT_TYPE_GROOVY){
			return GroovyUtil.executeGroovy((Class<?>)scriptCacheDto.getScriptContent(), param);
		}
		return null;
	}
	
	/**
	 * 缓存中是否存在指定的脚本缓存
	 * @param scriptCode
	 * @return
	 */
	public boolean scriptNotNull(String scriptCode){
		if(scriptCode == null) return false;
		Cache cache = cacheManager.getCache(ConstantUtil.CACHE_NAME_SCRIPT);
		ValueWrapper valueWrapper = cache.get(scriptCode);
		if(valueWrapper == null) return false;
		return true;
	}
	
	/**
	 * 系统启动时初始化脚本缓存
	 */
	@PostConstruct
	public void initAllScriptCache(){
		LOGGER.info("开始初始化脚本缓存...");
		List<ScriptDto> scriptList = scriptConfDao.getAllScript();
		for(ScriptDto scriptDto : scriptList){
			parseScriptAndSendToCache(scriptDto);
		}
		LOGGER.info("初始化脚本缓存完成!!!");
	}
	
}
