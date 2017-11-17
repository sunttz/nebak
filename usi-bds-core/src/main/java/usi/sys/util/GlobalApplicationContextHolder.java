package usi.sys.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 对全系统提供ApplicationContext及对应的getBean
 * @author lmwang
 * 创建时间：2014-7-2 上午10:06:02
 */
@Service
public class GlobalApplicationContextHolder implements ApplicationContextAware{
	
	//spring容器
	private static ApplicationContext context;

	/**
	 * 由于实现了ApplicationContextAware接口，当把此类配置为bean的时候，会自动注入ApplicationContext
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
//		context = applicationContext;
		//实例方法不建议访问类变量
		setAppContext(applicationContext);
	}

	/**
	 * 提供设置ApplicationContext的静态方法
	 * @param applicationContext
	 */
	public static void setAppContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}

	/**
	 * 静态方法获取ApplicationContext
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * 根据bean名称获取bean
	 * @param name bean名称
	 * @return
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}
	
	/**
	 * 根据Class获取bean（泛型）
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}
	
	/**
	 * 根据Class获取实现某接口的所有bean
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
		return getApplicationContext().getBeansOfType(clazz);
	}
}
