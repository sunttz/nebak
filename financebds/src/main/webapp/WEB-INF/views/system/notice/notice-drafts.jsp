<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>草稿箱</title>
		<link rel="stylesheet" type="text/css" href="${ctx }/res/theme-${uiInfo.theme }/notice/notice-drafts.css" >
		<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
		<script>
			var ctx = '${ctx }';
			var staffId = '${sessionScope.authInfo.staffId }';
		</script>
		<script type="text/javascript" src="${ctx }/res/javascript/system/notice/notice-drafts.js"></script>
	</head>
	<body>
		<div class="head">
			<div class="top_menu_wrap">
				<div class="menu">
					<b class="checkBox"></b>
					<!-- <b class="xiala"></b> -->
				</div>
				<div id="removeBtn" class="menu">删&nbsp;除</div>
				<div id="publishBtn" class="menu">发&nbsp;布</div>
				<div id="reloadBtn" class="menu">刷&nbsp;新</div>
			</div>
		</div>
		<div class="main">
			<div class="items">
			</div>
		</div>
		<div id="msgDialog" class="msgDialog">
		</div>
		<div id="loadDialog" class="loadDialog">
			<b></b><span></span>
		</div>
	</body>
</html>