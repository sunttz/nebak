package usi.sys.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 用apache工具类转义特殊字符< >等，防止xss
 * @author lmwang 创建时间：2014-12-10 下午3:48:54
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	// 只要参数名带inXssWhiteListQ，就不转义
	private static final String IN_XSS_WHITE_LIST_Q = "inXssWhiteListQ";

	/**
	 * 构造方法
	 * @param request
	 */
	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	/**
	 * 转义头
	 */
	@Override
	public String getHeader(String name) {
		return StringEscapeUtils.escapeHtml4(super.getHeader(name));
	}

	/**
	 * 转义包装请求对象
	 */
	@Override
	public String getQueryString() {
		return StringEscapeUtils.escapeHtml4(super.getQueryString());
	}

	/**
	 * 转义参数名
	 */
	@Override
	public String getParameter(String name) {
		return StringEscapeUtils.escapeHtml4(super.getParameter(name));
	}

	/**
	 * 转义参数值（此处不使用apache的工具类，因为工具类连双引号都转义了，影响系统功能）
	 */
	@Override
	public String[] getParameterValues(String name) {
		// 如果某次请求中，包含参数名为inXssWhiteListQ的参数，那么就不转义，否则只转义< >
		if (canOnGo()) {
			String[] values = super.getParameterValues(name);
			if (values != null) {
				int length = values.length;
				String[] escapseValues = new String[length];
				for (int i = 0; i < length; i++) {
					escapseValues[i] = cleanXSS(values[i]);
				}
				return escapseValues;
			}
		}
		return super.getParameterValues(name);
	}
	/**
	 * 判断是否需要转义
	 * @return true需要转义 false 不需要
	 */
	private boolean canOnGo() {
		if (super.getParameterMap().containsKey(IN_XSS_WHITE_LIST_Q)) {
			return false;
		}
		return true;
	}

	/**
	 * 决定只转义< >
	 * @param value 待转义
	 * @return
	 */
	private String cleanXSS(String value) {
		String val = value;
		// You'll need to remove the spaces from the html entities below
		val = val.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		// value = value.replaceAll("\\(", "& #40;").replaceAll("\\)",
		// "& #41;");
		// value = value.replaceAll("'", "& #39;");
		// value = value.replaceAll("eval\\((.*)\\)", "");
		// value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']",
		// "\"\"");
		// value = value.replaceAll("script", "");
		return val;
	}

}