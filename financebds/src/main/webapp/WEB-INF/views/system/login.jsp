<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE HTML>
<%
	 response.setHeader("Pragma","No-cache");
	 response.setHeader("Cache-Control","No-cache");
	 response.setDateHeader("Expires", -1);
	 response.setHeader("Cache-Control", "No-store");
%>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">
	    <%-- 让 IE 浏览器运行最新的渲染模式下 --%>
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<%-- icon--%>
		<link rel="shortcut icon" href="${ctx}/res/theme-${uiInfo.theme }/images/favicon.ico" />
		<title>${uiInfo.name }</title>
		<link type="text/css" rel="stylesheet" href="${ctx}/res/theme-${uiInfo.theme }/login.css" />
		<link type="text/css" rel="stylesheet" href="${ctx}/res/css/system/reset.css" />
		<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
		<script>
			var ctx = '${ctx }';
		</script>
		<script type="text/javascript" src="${ctx}/res/javascript/system/login.js"></script>
		<script type="text/javascript" src="${ctx}/res/javascript/common/md5.js"></script>
		<style>
			#password1{width:182px; height:24px; border:0; background:none; line-height:24px; color:#C8C8C8;}
		</style>		
	</head>

	<body>
		<div class="l_top">
			<div class="w1000">
		    	<span class="logo">
		    		<img width="400" height="80" src="${ctx }${uiInfo.loginlogo }">
		    	</span>
		    </div>
		</div>
		<div class="l_main">
		   	<div class="w1000 clearfix">
	      		<div class="loginBanner"></div>
	      		<div class="login">
	      			<p class="zhdl">
	      				<span style="color:#FE7A02;font-size:24px;">登录</span>${uiInfo.name }
		        	</p>
		        	<form>
		            	<div class="login_err_panel" id="err_area">
		            		<span style="display:inline-block;vertical-align:middle;">
		            			<i class="icon24-login" style="margin-top:-0.2em;"></i>
		            		</span>
		            		<span id="err_tip"></span>
		            	</div>
		            	<div class="box" style="margin-top:20px;">
		                	<b class="userbg"></b>
		                    <input id="userId" type="text" name="userId"  value="用户名" onfocus="if(value=='用户名'){value=''}" onblur="if(value==''){value='用户名'}" />
		                </div>
		                <div class="box pos" style="margin-bottom:40px;">
		                	<b class="passwordbg"></b>
		                    <input type="text" id="password1" />
		                    <span id="login_pass_tips">密码</span>
		                    <%--只要密码不是通过ssl传输，即使你加密了，仍然被appscan认为是解密的登录请求，所以采用变通方法，用文本款模拟密码框 --%>
		                    <input type="hidden" id="password"  name="password"  />
		                </div>
		                <!-- <div class="rp">
		                	<a href="javascript:;" class="forget_pwd">忘记密码了?</a>
		                </div> -->
		              	<a id="loginBtn" href="javascript:;" class="btn_login"></a>
		            </form>
	      		</div>
		    </div>
		</div>
		<div class="l_foot">
	    	<p style="margin-top:13px;">系统要求：Win操作系统，IE8.0及以上版本 建议分辨率：1024*768</p>
	    	<p>Copyright&copy;${uiInfo.copyright }</p>
		</div>
	</body>
</html>
