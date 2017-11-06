package usi.sys.util;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import org.codehaus.groovy.runtime.InvokerHelper;

public class GroovyUtil {

	/**
	 * 将groovy脚本编译成class
	 * @param groovyScript
	 * @return
	 */
	public static Class<?> parseClass(String groovyScript){
		ClassLoader parent = GroovyUtil.class.getClassLoader();
		GroovyClassLoader loader = new GroovyClassLoader(parent);
		Class<?> groovyClass = loader.parseClass(groovyScript);
		return groovyClass;
	}
	
	/**
	 * 执行groovy脚本
	 * 脚本中须要有名为groovyParam的变量接收入参
	 * @param groovyClass 脚本编译的class，一般放在缓存中
	 * @param param 入参，为保持代码的通用，只能传一个参数
	 * @return
	 */
	public static Object executeGroovy(Class<?> groovyClass, Object param){
		Binding binding = new Binding();
		binding.setVariable("$groovyParam", param);
		Script script = InvokerHelper.createScript(groovyClass, binding);
		Object result = script.run();
		return result;
	}
	
}
