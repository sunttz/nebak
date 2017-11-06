package usi.sys.filter;

import usi.sys.dto.AuthInfo;

/**
 * 
 * @Description 该类用于存放session中会被service或dao层用到的信息
 * @author zhang.dechang
 * @date 2015年8月1日 上午10:18:40
 */
public class SessionContent {
	private static ThreadLocal<AuthInfo> authInfoLocal = new ThreadLocal<AuthInfo>();
	
	public static AuthInfo getAuthInfoLocal(){
		return authInfoLocal.get();
	}
	
	public static void setAuthInfoLocal(AuthInfo authInfo){
		authInfoLocal.set(authInfo);
	}
	
    public static void clear() {
    	authInfoLocal.set(null);
    }
}
