package usi.sys.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求Ip
 * 
 * @author lmwang 2013-9-26 下午3:59:39
 */
public class IpAddressUtil {

	/**
	 * 根据请求获取ip
	 * 
	 * @param request
	 * @return ip地址
	 */
	public static String getReqIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null) {
			if (ip.indexOf(',') == -1) {
				return ip;
			}
			return ip.split(",")[0];
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();

		}
		return ip;
	}
}
