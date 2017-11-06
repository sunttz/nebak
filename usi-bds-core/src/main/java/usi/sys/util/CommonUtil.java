package usi.sys.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQSequence;

import net.sf.saxon.xqj.SaxonXQDataSource;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用工具类
 * @author fan.fan
 * @date 2014-3-31 上午11:18:45
 */
public class CommonUtil {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

	/**
	 * 验证一个字符串是否有值(既不是null,也不是空字符串)
	 * @param value
	 * @return
	 */
	public static final boolean hasValue(String value) {
		return value != null && value.trim().length() > 0;
	}

	/**
	 * 调用加密工具类进行加密，加密工具类可被使用此框架的系统覆盖
	 * @param s
	 * @return 加密后字符串
	 */
	public static final String getMd5(String s) {
		return EncryptUtil.doEncrypt(s);
	}

	/**
	 * 格式化日期（采用阿帕奇的工具类，性能好，线程安全）
	 * @param date 日期
	 * @param formatPattern 要格式化的形式
	 * @return
	 */
	public static final String format(Date date, String formatPattern) {
		if (date == null) {
			return "";
		}
		return DateFormatUtils.format(date, formatPattern);
	}

	/**
	 * 将字符串按格式，格式化成日期（采用阿帕奇的工具类，性能好，线程安全）
	 * @param stringValue 字符串日期
	 * @param formatPattern 要格式化成的日期
	 * @return
	 */
	public static final Date parse(String stringValue, String formatPattern) {
		if (stringValue == null || formatPattern == null) {
			return null;
		}
		Date finalDate = null;
		try {
			finalDate = DateUtils.parseDate(stringValue, new String[]{formatPattern});
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return finalDate;
	}

	/**
	 * 获取主机名
	 * @return
	 */
	public static String getLocalHostName() {
		String hostName;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		} catch (Exception e) {
			e.printStackTrace();
			hostName = "";
		}
		return hostName;
	}

	/**
	 * 返回ip地址（如果是多网卡，ip地址用逗号分割）
	 * @return
	 */
	public static String getAllLocalHostIP() {

		StringBuilder sb = new StringBuilder("");
		try {
			String hostName = getLocalHostName();
			if (hostName.length() > 0) {
				InetAddress[] addrs = InetAddress.getAllByName(hostName);
				if (addrs.length > 0) {
					for (int i = 0; i < addrs.length; i++) {
						sb.append(addrs[i].getHostAddress());
						sb.append(",");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 替换输入模板中的参数,参数格式{变量}
	 * 变量在入参模板中是用##包起来
	 */
	public static String fillTemplateData(Map<String, Object> params, String template) throws Exception {
		String temp = template;
		// 替换入参模版中的占位符
		String matchStr = "##\\w+##";
		Pattern localPattern = Pattern.compile(matchStr);
		Matcher localMatcher = localPattern.matcher(temp);
		while (localMatcher.find()) {
			int i = localMatcher.start();
			int j = localMatcher.end();
			// 原字符串拆分
			String paramName = temp.substring(i + 2, j - 2);
			String replaceStr = "##" + paramName + "##";
			// 内存中哈希Map
			String retValue = (String) params.get(paramName);
			// 赋值（如果内存中没有该值，赋为""）
			if (retValue == null) {
				throw new IllegalArgumentException("替换模板时,{" + paramName + "}参数找不到!");
			}
			temp = temp.replaceAll(replaceStr, retValue);
			localMatcher = localPattern.matcher(temp);
		}
		return temp;
	}
	
	/**
	 * 在用表达式替换掉接口模板参数后在调用需要用XQUERY解析的地方解析入参
	 * xquery脚本在入参模板中是用Φ包起来
	 * @param template
	 * @return
	 * @throws XQException
	 */
	public static String execXqueryReplaceInParam(String paramStr) throws XQException {
		String tempStr = paramStr;
		Pattern pattern = Pattern.compile("Φ.*?Φ", Pattern.DOTALL);
		Matcher localMatcher1 = pattern.matcher(tempStr);
		while (localMatcher1.find()) {
			int i = localMatcher1.start();
			int j = localMatcher1.end();
			// 原字符串拆分
			String xquery = tempStr.substring(i + 1, j - 1);
			String startStr = tempStr.substring(0, i);
			String endStr = tempStr.substring(j, tempStr.length());

			XQDataSource ds = new SaxonXQDataSource();
			XQConnection conn = ds.getConnection();
			XQExpression exp = conn.createExpression();
			exp.bindDocument(XQConstants.CONTEXT_ITEM, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root></root>", null,null);
			XQSequence result = exp.executeQuery(xquery);
			result.next();
			tempStr = startStr + result.getItemAsString(null) + endStr;// 接收xquery查询结果
			localMatcher1 = pattern.matcher(tempStr);
		}
		return tempStr;
	}
	
	public static String filetoStr(String name) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(CommonUtil.class.getResourceAsStream(name), "UTF-8"));
		String temp;
		try {
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\n");
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}
	/**
	 * 获取uuid
	 * 默认是小写的,不带"-"
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
}
