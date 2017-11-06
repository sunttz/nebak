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

import usi.sys.util.ConstantUtil;

/**
 * 拦截未登录的请求
 * @author fan.fan
 * @date 2014-4-3 上午10:40:46
 */
public class CheckSessionFilter extends HttpServlet implements Filter {

	private static final long serialVersionUID = 1L;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if(request.getSession().getAttribute(ConstantUtil.AUTH_INFO) == null) {
			request.setCharacterEncoding("UTF-8");
	        response.setCharacterEncoding("UTF-8");
	        response.setContentType("text/html;charset=UTF-8");
	        
			String path = request.getContextPath();
    		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        	PrintWriter out = response.getWriter();
            StringBuilder builder = new StringBuilder();
            builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
            builder.append("alert(\"登录超时，请重新登录！\");");
            builder.append("window.top.location.href=\"");
            builder.append(basePath);
            builder.append("\";</script>");
            out.print(builder.toString());
            out.close();
            return;
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
