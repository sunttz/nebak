package usi.sys.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 读取classpath下面config.properties文件的工具类
 * @author lmwang
 * 创建时间：2014-7-14 上午10:16:15
 */
public class ConfigUtil {
	//初始化配置文件
	private static Properties pro = new Properties();
	static{
		Resource resource = new ClassPathResource("config.properties");
		InputStream ips=null;
		try {
			ips = resource.getInputStream();
			pro.load(ips);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{//lmwang 20131223关闭输入流
			try {
				if(ips!=null){
					ips.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	//获取配置文件的中配置的值
	public static String getValue(String key){
		return pro.getProperty(key).trim();
	}
}
