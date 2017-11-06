package usi.sys.sync.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import usi.sys.dto.DictDto;
import usi.sys.service.BusiDictService;

/**
 * @Description 
 * @author zhang.dechang
 * @date 2015年6月4日 下午8:22:57
 * 
 */
@Service
public class NodeSyncService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NodeSyncService.class);
	
	@Resource
	private BusiDictService busiDictService;
	
	/**
	 * 
	 * @param context 工程上下文
	 * @param uri 请求的url
	 * @param paramsMap 参数键值对,若没有参数，可传null
	 */
	public void invokeHttpclient4Sync(String uri, Map<String,String> paramsMap){
		LOGGER.info("开始进行节点同步,url={}",uri);
		List<DictDto> nodes = busiDictService.getDictByCode("CLUSTER_NODE");
		HttpClient client = new DefaultHttpClient();
		// 设置连接超时
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		// 设置返回超时时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		try {
			//请求参数
			UrlEncodedFormEntity formEntity = null;
			if(paramsMap != null){
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for(Map.Entry<String, String> entry : paramsMap.entrySet()){
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				formEntity = new UrlEncodedFormEntity(params, "UTF-8");
			}
			for(DictDto dictDto : nodes){
				try {
					String url = dictDto.getDicCode() + uri;
					HttpPost post = new HttpPost(url);
					if(formEntity != null){
						post.setEntity(formEntity);
					}
					HttpResponse response = client.execute(post);
					EntityUtils.consume(response.getEntity());
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.info("节点【{}】连接出现问题",dictDto.getDicCode());
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
//			 关闭连接
			client.getConnectionManager().shutdown();
		}
	}
}
