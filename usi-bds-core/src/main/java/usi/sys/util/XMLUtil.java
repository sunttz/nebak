package usi.sys.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQSequence;

import net.sf.saxon.xqj.SaxonXQDataSource;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLUtil {

	protected static final Logger LOGGER = LoggerFactory.getLogger(XMLUtil.class);

	private static XQDataSource ds = new SaxonXQDataSource();

	/**
	 * @param context
	 * @param script
	 * @return
	 * @throws XQException
	 */
	public static String executeXquery(String context, String script) throws XQException {
		XQConnection conn = null;
		XQExpression exp = null;
		XQSequence sequence = null;
		String result = null;
		conn = ds.getConnection();
		exp = conn.createExpression();
		exp.bindDocument(XQConstants.CONTEXT_ITEM, context, null, null);
		sequence = exp.executeQuery(script);
		if (sequence.next()) {
			result = sequence.getItemAsString(null);
		}
		return result;
	}

	/**
	 * @param script
	 * @return
	 */
	public static String executeXquery(String script) throws XQException {
		XQConnection conn = null;
		XQExpression exp = null;
		XQSequence sequence = null;
		String result = null;
		conn = ds.getConnection();
		exp = conn.createExpression();
		sequence = exp.executeQuery(script);
		if (sequence.next()) {
			result = sequence.getItemAsString(null);
		}
		return result;
	}

	/**
	 * 格式化输出xml
	 * @param inputXML
	 * @return
	 * @throws Exception
	 */
	public static String formatXML(String inputXML) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(inputXML));
		String requestXML = null;
		XMLWriter writer = null;
		StringWriter out = null;
		if (document != null) {
			try {
				OutputFormat formate = OutputFormat.createPrettyPrint();
				formate.setSuppressDeclaration(true);
				formate.setEncoding("UTF-8");
				out = new StringWriter();
				writer = new XMLWriter(out, formate);
				writer.write(document);
				requestXML = out.toString();
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		return requestXML;
	}
}
