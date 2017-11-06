package usi.sys.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import usi.sys.dto.AuthInfo;
import usi.sys.util.ConstantUtil;

/**
 * @Description 将session中会被service或dao层用到的信息放入threadlocal
 * 一般只拦截.do的url (所以以.action结尾的url若内含登陆逻辑,其调用的service层中取不到SessionContent)
 * @author zhang.dechang
 * @date 2015年4月15日 上午10:02:07
 * 
 */
public class GetSessionContentFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpSession httpSession = req.getSession(false);
		try {
			if(httpSession != null){
				AuthInfo authInfo = (AuthInfo)httpSession.getAttribute(ConstantUtil.AUTH_INFO);
				SessionContent.setAuthInfoLocal(authInfo);
			}
			chain.doFilter(request, response);
		} finally {
			SessionContent.clear();
		}
	}

	@Override
	public void destroy() {
		//nothing to do
	}

}
