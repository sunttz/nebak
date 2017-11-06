package usi.sys.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class UiLoaderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		Map<String, String> uiInfo = new HashMap<String, String>();
		InputStream is = null;
		try {

			/*
			 * 读取ui配置信息到servletContext
			 */
			String path = this.getServletConfig().getInitParameter("uiConfigLocation").substring("classpath:".length());
			SAXReader reader = new SAXReader();
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			Document doc = reader.read(is);
			Element application = (Element) doc.selectObject("/application");
			Element loginLogo = (Element) doc.selectObject("/application/login-page/logo-url");
			Element indexLogo = (Element) doc.selectObject("/application/index-page/logo-url");

			uiInfo.put("theme", application.attributeValue("theme"));
			uiInfo.put("name", application.attributeValue("name"));
			uiInfo.put("copyright", application.attributeValue("copyright"));
			uiInfo.put("loginlogo", loginLogo.getStringValue());
			uiInfo.put("indexlogo", indexLogo.getStringValue());

			this.getServletContext().setAttribute("uiInfo", uiInfo);
		} catch (Exception e) {
			/*
			 * 配置文件如果解析失败，设置默认信息
			 */
			e.printStackTrace();
			uiInfo.put("theme", "dianxin");
			uiInfo.put("name", "科大国创系统框架");
			uiInfo.put("copyright", "2014 科大国创 版权所有");
			uiInfo.put("loginlogo", "/res/theme-dianxin/images/login/logo.png");
			uiInfo.put("indexlogo", "/res/theme-dianxin/images/index/logo_small.png");
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
