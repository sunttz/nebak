package usi.sys.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import usi.sys.util.ConfigUtil;

/**
 * 菜单访问过滤器（如果当前访问是get请求，且以*Menu.do结尾[约定]，说明是菜单，此时与session中的登录人拥有的叶子菜单做比较）。注：此为粗粒度，细粒度应该某菜单里涉及的url都不让访问
 * 备注：约定所有菜单都以*Menu.do结尾
 * @author lmwang
 * 创建时间：2014-12-15 下午2:26:17
 */
public class MenuAccessFilter implements Filter {
	
	private ServletContext servletContext;
	
	private static final String MENU_FILTER = "menuFilter";
	/**
	 * 初始化时，设置context，后来会用来取全局变量
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.servletContext = filterConfig.getServletContext();
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		//在web.xml中配置的初始化参数
		String menuFilterInitParameter = servletContext.getInitParameter(MENU_FILTER);
		if("true".equals(menuFilterInitParameter)) {
			//如果是get请求
			if("GET".equals(request.getMethod())) {
				//请求的servlet路径例如/a/b.do
				String servletPath = request.getServletPath();
				//是菜单
				if(servletPath.indexOf("Menu.do")>-1) {
					//问号后面都去掉
					if(servletPath.indexOf("?")>-1) {
						servletPath = servletPath.substring(0,servletPath.indexOf("?"));
					}
					//session里保存的叶子菜单map
					@SuppressWarnings("unchecked")
					Map<String,String> leafMenusMap = (Map<String,String>)request.getSession().getAttribute(ConfigUtil.getValue("menuKey"));
					if(leafMenusMap!=null) {
						//是否是自己的菜单
						if(!leafMenusMap.containsKey(servletPath)) {
				    		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/404";
				        	PrintWriter out = response.getWriter();
				            StringBuilder builder = new StringBuilder();
				            builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
				            builder.append("window.top.location.href=\"");
				            builder.append(basePath);
				            builder.append("\";</script>");
				            out.print(builder.toString());
				            out.close();
				        //有权限
						}else {
							chain.doFilter(req, res);
						}
			        //取不到
					} else {
						System.out.println("没有配置角色。。。");
						chain.doFilter(req, res);
					}
				//非菜单url，放过
				}else {
					chain.doFilter(req, res);
				}
			//非get请求
			}else {
				chain.doFilter(req, res);
			}
		//未开启过滤，放过	
		}else {
			chain.doFilter(req, res);
		}
	}
	/**
	 * 销毁时置空
	 */
	@Override
	public void destroy() {
		this.servletContext = null; 
	}
}
