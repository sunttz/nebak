package usi.sys.sync.util;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * @Description 清除cacheManager所管理的缓存
 * @author zhang.dechang
 * @date 2015年1月17日 下午2:32:51
 * 
 */
@Service
public class CacheSyncService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource
	protected CacheManager cacheManager;

	/**
	 * 清除指定缓存
	 */
	public void evictCache(String cacheName,String chcheKey) {
		Cache cache = cacheManager.getCache(cacheName);
		if(cache != null){
			cache.evict(chcheKey);
		}
		log.info("清除缓存,cacheName={}&chcheKey={}",cacheName,chcheKey);
	}
	
	/**
	 * 清除指定缓存
	 */
	public void evictCache(String cacheName,String[] chcheKeys) {
		Cache cache = cacheManager.getCache(cacheName);
		if(cache != null){
			for(String key : chcheKeys){
				cache.evict(key);
			}
		}
		log.info("清除缓存,cacheName={}&chcheKeys={}",cacheName,Arrays.toString(chcheKeys));
	}
	
	/**
	 * 清除指定缓存
	 */
	public void evictCache(String cacheName,List<String> chcheKeys) {
		Cache cache = cacheManager.getCache(cacheName);
		if(cache != null){
			for(String key : chcheKeys){
				cache.evict(key);
			}
		}
		log.info("清除缓存,cacheName={}&chcheKeys={}",cacheName,chcheKeys);
	}
	
	/**
	 * 清空指定的缓存
	 */
	public void clearCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if(cache != null){
			cache.clear();
		}
		log.info("清空缓存,cacheName={}",cacheName);
	}

}
