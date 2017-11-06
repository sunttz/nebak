package usi.sys.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 安全：1、检查referer是否来自本站点2、设置cookie的httponly
 * @author lmwang
 * 创建时间：2014-11-15 下午2:51:32
 */
public class SetCookieChkRefererFilter extends HttpServlet implements Filter{
	 private static final long serialVersionUID = 1L;
	 private static final String HTTP_ONLY = "HTTPOnly";
	 private static final String SET_COOKIE = "Set-Cookie";
	/**
	 * 空的init方法
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	/**
	 * 实现cookie设置httponly和referer检查
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
//		System.out.println("Referer==="+request.getHeader("Referer"));
//		System.out.println("getContextPath==="+request.getContextPath());
//		System.out.println("getRequestURL==="+request.getRequestURL());
		//应用上下文
		String contextPath = request.getContextPath();
		//请求的referer
		String referer = request.getHeader("Referer");
		
		

		//防止csrf跨站点请求伪造
		if(referer==null || referer.indexOf(contextPath)>=0) {
			
			//这个对下面的判断header有影响，一定要有（不清楚原因）
			if(request.getSession().isNew()) {
				String sessionId = request.getSession().getId();
//				System.out.println("sessionId==="+sessionId);
				//安全：解决用户的cookie可能被盗用的问题,减少跨站脚本攻击  
				String cookieValue = "JSESSIONID=" + sessionId + ";Path=" +contextPath+ ";"+ HTTP_ONLY;
				response.setHeader(SET_COOKIE, cookieValue);
			}
			
			chain.doFilter(req, res);
			
		//referer不是本站点，访问一个不存在且是WEB-INF下面的资源，系统默认转到404	
		}else {
    		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+contextPath+"/WEB-INF/views/GOT_U.jsp";
        	PrintWriter out = response.getWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
            builder.append("window.top.location.href=\"");
            builder.append(basePath);
            builder.append("\";</script>");
            out.print(builder.toString());
            out.close();
		}
	}
}
