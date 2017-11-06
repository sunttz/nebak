package usi.sys.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * 特殊符号进行转义防止js脚本攻击
 * @author lmwang
 * 创建时间：2014-12-9 下午3:41:53
 */
public class SpecialChrEscapeFilter  extends HttpServlet implements Filter{
		private static final long serialVersionUID = 1L;
		/**
		 * 空的init方法
		 */
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
		}
		/**
		 * 特殊符号进行转义防止js脚本攻击
		 */
	    @Override  
	    public void doFilter(ServletRequest request, ServletResponse response,  
	            FilterChain chain) throws IOException, ServletException {  
	        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);  
	    }  
		
}
