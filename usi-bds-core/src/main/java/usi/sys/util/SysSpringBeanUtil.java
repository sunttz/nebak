package usi.sys.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import usi.sys.job.service.ITask;

/**
 * 根据接口名称获取实现此接口的bean#method名称列表、bean名称列表
 * @author lmwang
 * 创建时间：2014-7-2 上午10:27:29
 */
public class SysSpringBeanUtil {
	
	/**
	 * 根据接口名称获取实现此接口的bean名称列表
	 * @param type 接口名称
	 * @return
	 */
	public static List<String> getSpringBeanByInterface(String type) {
		
		List<String> list = null;
		if("ITask".equals(type)) {
			Map<String, ITask> map = GlobalApplicationContextHolder.getBeansOfType(ITask.class);
			list = parseBeanInfo(map);
		} 
		return list;
		
	}
	
	/**
	 * 根据map解析成bean#method名称列表（反射）
	 * @param map 包含bean名称为键，泛型T（接口）
	 * @return
	 */
	@SuppressWarnings("unused")
	private static <T> List<String> parseBeanMethodInfo(Map<String, T> map) {
		
		List<String> list = new ArrayList<String>();
		Set<Entry<String, T>> set = map.entrySet();
		for(Entry<String, T> entry : set) {
			String beanName = entry.getKey();
			T bean = entry.getValue();
			Method[] methods = bean.getClass().getDeclaredMethods();
			for(Method method : methods) {
				list.add(beanName + "#" + method.getName());
			}
		}
		return list;
		
	}
	
	/**
	 * 根据map解析成bean名称列表
	 * @param map 包含bean名称为键，泛型T（接口）
	 * @return
	 */
	private static <T> List<String> parseBeanInfo(Map<String, T> map) {
		
		List<String> list = new ArrayList<String>();
		Set<Entry<String, T>> set = map.entrySet();
		for(Entry<String, T> entry : set) {
			
			String beanName = entry.getKey();
			list.add(beanName);
			
		}
		return list;
	}
	
}
