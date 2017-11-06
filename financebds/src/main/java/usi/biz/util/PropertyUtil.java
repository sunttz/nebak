package usi.biz.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * 配置文件读取工具类 .
 * 
 * @author zj
 */
public class PropertyUtil {

	/**
	 * 日志.
	 */
	private static Logger _logger = LoggerFactory.getLogger(PropertyUtil.class);

	/**
	 * 配置文件的名称.
	 */
	private static String propertyName = "config.properties";

	/**
	 * 配置文件引用.
	 */
	private static Properties p = null;

	/**
	 * 加载配置文件.
	 */
	public static void load() {
		_logger.debug("#加载配置文件{}", propertyName);
		ClassPathResource resource = new ClassPathResource(propertyName);
		try {
			InputStream in = resource.getInputStream();
			if (in == null) {
				_logger.error("#配置文件{}不存在", propertyName);
			} else {
				p = new Properties();
				p.load(in);
			}
		} catch (IOException e) {
			_logger.error("#加载配置文件{}出错", propertyName);
			e.printStackTrace();
		}
	}

	/**
	 * 类加载时加载配置文件
	 */
	static {
		load();
	}

	/**
	 * 获取Key对应的value.
	 * 
	 * @param key
	 *            关键字
	 * @return 结果
	 */
	public static String getStringValue(String key) {
		return p == null ? null : p.getProperty(key);
	}

	/**
	 * 获取Key对应的value.
	 * 
	 * @param key
	 *            关键字
	 * @return 结果
	 */
	public static boolean getBooleanValue(String key) {
		return p == null ? null
				: "true".equalsIgnoreCase(p.getProperty(key)) ? true : false;
	}

	/**
	 * 获取Key对应的value.
	 * 
	 * @param key
	 *            关键字
	 * @return 结果
	 */
	public static int getIntValue(String key) {
		return p == null ? null : Integer.parseInt(p.getProperty(key));
	}
}