<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
	 response.setHeader("Pragma","No-cache");
	 response.setHeader("Cache-Control","No-cache");
	 response.setDateHeader("Expires", -1);
	 response.setHeader("Cache-Control", "No-store");
%>
<!DOCTYPE HTML>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		 <%-- 让 IE 浏览器运行最新的渲染模式下 --%>
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
 		<%-- icon--%>
		<link rel="shortcut icon" href="${ctx}/res/theme-${uiInfo.theme }/images/favicon.ico" />
		<title>${uiInfo.name }</title>
		<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
		<script type="text/javascript" src="${ctx}/res/javascript/common/util.js"></script>
		<script type="text/javascript" src="${ctx}/res/javascript/common/md5.js"></script>
		
		<link type="text/css" rel="stylesheet" href="${ctx}/res/theme-${uiInfo.theme }/index.css" />

		<script>
			var ctx = '${ctx }';
			var authMenus = ${menusStr};
			var lv1MenuNum = ${lv1MenuNum};
		</script>
		<script type="text/javascript" src="${ctx}/res/javascript/system/index.js"></script>
		<!--[if IE 6]>
		<script type="text/javascript" src="${ctx}/res/javascript/common/DD_belatedPNG.js"></script>
		<script type="text/javascript">
		DD_belatedPNG.fix('.h_xiala,.pullbar_l,.pullbar_r');
		</script>
		<![endif]-->
	</head>
	<body>
		<!-- head begin -->
		<div class="head">
			<div class="h_top">
				<div class="h_logo">
					<img width="97" height="43" src="${ctx }${uiInfo.indexlogo}">
				</div>
				<div class="sysname">网元备份管理</div>
				<ul class="h_content">
					<li class="content_item welname">
						欢迎您，${sessionScope.authInfo.userName }
					</li>
					<li class="content_item">
					</li>
					<li class="content_item pwd">
						<span style="display: none;" id="userId">${sessionScope.authInfo.userId }</span>
						<a id="pwd_btn" href="javascript:;">密码修改</a>
						<div id="pwd_panel">
							<div class="pwd_title">
								旧的密码：<input id="oldPwd" type="password" /><p style="height:10px;"></p>
								新的密码：<input id="newPwd" type="password" /><p style="height:10px;"></p>
								密码确认：<input id="againPwd" type="password" />
							</div>
							<div id="warn_area" class="pwd_warn">
								<span style="display:inline-block;">
			            			<i class="pwd_error"></i>
			            		</span>
			            		<span id="warn_tip" class="red"></span>
							</div>
							<div class="pwd_tail">
								<div id="pwd_close" class="tail_btn">
									关闭
								</div>
								<div id="pwd_submit" class="tail_btn">
									保存
								</div>
							</div>
						</div>
					</li>
					<!-- <li class="content_item">
						|
					</li>
					<li class="content_item">
						<a href="javascript:;">设置</a>
					</li> -->
					<li class="content_item">
						|
					</li>
					<li class="content_item">
						<a id="logout_btn" href="javascript:;">退出</a>
					</li>
				</ul>
			</div>
			<div class="h_tail">
				<div class="h_nvg clearfix">
					<c:forEach items="${menus}" end="${lv1MenuNum-1}" var="menu">
						<!-- 加a标签只是为了在ie6下能够让伪类hover有效果，没有其他任何作用 -->
						<a style="color:#6A6A6A" href="####"><div onclick="tabClick(this);" menuId="${menu.menuId }" id="${menu.menuId }_tab" title="${menu.menuName }" class="h_tab">${menu.menuName }</div></a>
		            </c:forEach>
		            <div class="h_xiala" tabindex="0" >
		            	<div class=h_xiala_panel>
		            		<%-- 此处iframe只是为了在ie6下能让浮层div遮住select，没有其他作用 --%>
<!-- 		            		<iframe frameborder="0" style="position:absolute;width:100%;z-index:-1;"></iframe> -->
		            		<%-- 加a标签只是为了在ie6下能够让伪类hover有效果，没有其他任何作用 --%>
		            		<a style="color:#6A6A6A" href="####">
		            			<div id="close_item" class="xia_item">关闭全部</div>
		            		</a>
		            		<div class="default_items">
			            		<div class="xia_split"></div>
		            		</div>
		            	</div>
		            </div>
				</div>
			</div>
		</div>
		<%-- head end --%>
		
		<%-- left begin --%>
		<div class="left">
			<ul class="l_menu"></ul>
			<a class="pullbar_l" href="javascript:;"></a>
		</div>
		<%-- left end --%>

		<%-- center begin--%>
		<div class="center">
			<iframe name="mainFrame" scrolling="auto" frameborder="0"></iframe>
			<a class="pullbar_r" href="javascript:;"></a>
		</div>
		<%--center end --%>
	</body>
</html>
