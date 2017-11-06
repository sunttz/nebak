<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>上线公告管理</title>
		<link rel="stylesheet" type="text/css" href="${ctx }/res/theme-${uiInfo.theme }/notice/notice-all.css" >
		<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
		<script type="text/javascript" src="${ctx}/res/javascript/common/json2.js"></script>
		 <!-- My97DatePicker -->
	    <script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
		<script>
			var ctx = '${ctx }';
			var staffId = '${sessionScope.authInfo.staffId }';
			var theme = '${uiInfo.theme }';
		</script>
		<script type="text/javascript" src="${ctx }/res/javascript/system/notice/notice-all.js"></script>
	</head>
	<body>
		<div class="head">
			<div class="top_menu_wrap">
				<div id="removeBtn" class="menu first">删&nbsp;除</div>
				<div id="lookBtn" class="menu" tabindex="0">
					<b class="plaintext">查&nbsp;看</b>
					<b class="xiala"></b>
					<div class="xialapanel">
						<div id="lookAllBtn" class="xiaitem">全部</div>
						<div id="lookOnBtn" class="xiaitem">在线公告</div>
						<div id="lookOffBtn" class="xiaitem">下线公告</div>
					</div>
				</div>
			</div>
		</div>
		<div class="main">
			<div class="table_head">
				<div class="title title1">
					<b class="checkBox"></b>
				</div>
				<div class="title title2">
					标题
				</div>
				<div class="title titleRe"></div>
				<div class="title title3">
					发布时间
				</div>
				<div class="title title4">
					下线时间
				</div>
				<div class="title title5">
					浏览次数
				</div>
				<div class="title title6">
					回复次数
				</div>
				<div class="title title7">
					发布人
				</div>
			</div>
			<div class="items">
			</div>
		</div>
		<div id="msgDialog" class="msgDialog">
		</div>
		<div id="loadDialog" class="loadDialog">
			<b></b><span></span>
		</div>
		<div id="publishBox" class="msgbox">
			<div class="msgbox-hd">
				<span class="msgbox-title"></span>
				<b class="msgbox-close" onclick="closeMsgbox();"></b>
			</div>
			<div class="msgbox-bd">
				<div class="msgbox-normal">
					<div class="msgbox-fm">
						<div class="label">输入下线时间</div>
						<div class="ipt">
							<input id="loseTime" class="ipt-input" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" class="Wdate" readonly="readonly" />
						</div>
					</div>
					<div></div>
				</div>
			</div>
			<div class="msgbox-ft">
				<div class="desc">
					默认不选，永不失效
				</div>
				<div class="menu" onclick="closeMsgbox();">取&nbsp;消</div>
				<div class="menu first" onclick="submitMsgbox();">确&nbsp;定</div>
			</div>
		</div>
		<div class="mask"></div>
	</body>
</html>