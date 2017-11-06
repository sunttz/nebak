package usi.sys.key.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import usi.sys.util.ConstantUtil;

@Service
public class PrimaryKeyFactory implements ApplicationContextAware {
	private static Map<String, SingleSequence> singleSequenceMap = new ConcurrentHashMap<String, SingleSequence>();
	private static PrimaryKeyFactory factory;
	
	@Resource
	private UniqueTableApp defaultUniqueTableApp;
	private ApplicationContext context;
	private int cacheKeyNum = ConstantUtil.PRIMARY_KEY_CACHE_COUNT;
	
	private PrimaryKeyFactory() {}
	
	public static SingleSequence getSequence(String name) {
		SingleSequence sequence = (SingleSequence) singleSequenceMap.get(name);
		if (sequence == null) {
			synchronized(PrimaryKeyFactory.class) { 
				if (sequence == null) {
					sequence = factory.createSequence(name);
					singleSequenceMap.put(name, sequence);
				}
			}
		}
		return sequence;
	}

	private SingleSequence createSequence(String name) {
		int cacheNum = getCacheKeyNum();
		SingleSequence sequence = new SingleSequence(cacheNum, defaultUniqueTableApp);
		return sequence;
	}

	public void setUniqueTableApp(UniqueTableApp uniqueTableApp) {
		this.defaultUniqueTableApp = uniqueTableApp;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
		factory = this.context.getBean(PrimaryKeyFactory.class);
	}

	public int getCacheKeyNum() {
		return cacheKeyNum;
	}

	public void setCacheKeyNum(int cacheKeyNum) {
		this.cacheKeyNum = cacheKeyNum;
	}
}
