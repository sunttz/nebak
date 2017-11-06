package usi.biz.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

public class UnGZipUtil {

	/**
	 * <p>gZip解密和解压缩为String</p>
	 * 
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	public static String unGZipToString(String resultInfo) throws IOException{
		String unGZipData = null;
		try{
			byte[] data	= (new BASE64Decoder()).decodeBuffer(resultInfo);//解密
			//解压缩 
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			GZIPInputStream gzipis = new GZIPInputStream(bais);
			unGZipData = IOUtils.toString(gzipis,"GBK");	
			gzipis.close();
			bais.close();
		}catch(IOException e){
			String exceptionId = "Exception.unGZip";
			String exceptionInfo = "GZip解压异常" + exceptionId + "\n----" + e.getMessage();
			throw new IOException(exceptionInfo);
		}
		return unGZipData;
	}
	
	//测试解密解压缩方法
	public static void main(String[] args) {
		UnGZipUtil unGZip = new UnGZipUtil();
		String resultInfo ="H4sIAAAAAAAAAD3PbWuDMBQF4P8i7Ns6cxOjRhjDWl9a1nVhdhTGKJnJmFNMl2pVxv77RNjgHjg8\r\nn859+baSx2xrBRZgxyMYsHU9yWoWihAiFAOa9STf96ae/KNtT4FtA7oBZwqbjgUMMbDH8byQw0Lq\r\nvqm1kPZfiXTTGl3XyvzTnVFfnTq3t/E974yzSt62hvJUFd0uh2Ibqrjk63B3WF6KB8fbo88L4DwZ\r\nnseqDpmgEUpzHh82vfB60gzEX/JQmY0eKr5+Ujo8Hq9onKlSpDLL4mm9aNoyEZWKtFTzby5BmIFP\r\nXQeDz8AFYv28/gJ2xBLkDwEAAA==";
		String result = "{\"ifResult\":\"0\",\"ifWarning\":\"\",\"ifError\":\"\",\"ifErrorStack\":\"\",\"ifResultInfo\":\"H4sIAAAAAAAAAD3PbWuDMBQF4P8i7Ns6cxOjRhjDWl9a1nVhdhTGKJnJmFNMl2pVxv77RNjgHjg8\r\nn859+baSx2xrBRZgxyMYsHU9yWoWihAiFAOa9STf96ae/KNtT4FtA7oBZwqbjgUMMbDH8byQw0Lq\r\nvqm1kPZfiXTTGl3XyvzTnVFfnTq3t/E974yzSt62hvJUFd0uh2Ibqrjk63B3WF6KB8fbo88L4DwZ\r\nnseqDpmgEUpzHh82vfB60gzEX/JQmY0eKr5+Ujo8Hq9onKlSpDLL4mm9aNoyEZWKtFTzby5BmIFP\r\nXQeDz8AFYv28/gJ2xBLkDwEAAA==\"}";
		try {
			result= result.replaceAll("\r\n", "@");
			JSONObject interFaceResult=JSONObject.fromObject(result);
			String ifResultInfoString=interFaceResult.getString("ifResultInfo");
			ifResultInfoString= ifResultInfoString.replaceAll("@", "\r\n");
            String	reslutInfo = UnGZipUtil.unGZipToString(ifResultInfoString);//对ifResultInfo解密解压缩
            JSONArray array1 = JSONArray.fromObject(reslutInfo); 
        	JSONObject ifResultInfo=JSONObject.fromObject(array1.get(0));
			String res= unGZip.unGZipToString(resultInfo);
			System.out.println("res========"+res);
			System.out.println("re1========"+ifResultInfo.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
