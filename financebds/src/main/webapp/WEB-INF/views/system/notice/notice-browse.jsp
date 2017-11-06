<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>
			公告阅读：
			<c:choose>
				<c:when test="${empty notice.noticeTitle }">
					无标题
				</c:when>
				<c:when test="${!empty notice.noticeTitle }">
					${notice.noticeTitle }
				</c:when>
			</c:choose>
		</title>
		<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
		<!-- jPages -->
		<link type="text/css" rel="stylesheet" href="${ctx}/res/jPages/jPages.css" />
		<script type="text/javascript" src="${ctx}/res/jPages/jPages.min.js"></script>
		<!-- umeditor -->
		<link href="${ctx }/res/umeditor/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
	    <script type="text/javascript" charset="utf-8" src="${ctx }/res/umeditor/umeditor.config.js"></script>
	    <script type="text/javascript" charset="utf-8" src="${ctx }/res/umeditor/umeditor.min.js"></script>
	    <script type="text/javascript" src="${ctx }/res/umeditor/lang/zh-cn/zh-cn.js"></script>
	    <!-- self -->
		<link type="text/css" rel="stylesheet" href="${ctx}/res/theme-${uiInfo.theme }/notice/notice-browse.css" />
	    <script>
	    	var ctx = '${ctx }';
	    	var noticeId = '${notice.noticeId }';
	    	var user = ${sessionScope.authInfo };
	    	var replyFlag = '${notice.replyFlag }';
	    </script>
		<script type="text/javascript" src="${ctx }/res/javascript/system/notice/notice-browse.js"></script>
	</head>
	<body style="position:relative;">
		<div class="head">
			<div class="h_logo">
				<img width="400" height="80" src="${ctx }${uiInfo.loginlogo }">
			</div>
		</div>
		<div class="bd">
			<!-- 公告区 -->
			<div class="main">
				<div class="main-panel">
					<h3>
						<c:choose>
							<c:when test="${empty notice.noticeTitle }">
								无标题
							</c:when>
							<c:when test="${!empty notice.noticeTitle }">
								${notice.noticeTitle }
							</c:when>
						</c:choose>
					</h3>
					<div class="content">
						${notice.noticeContent }
					</div>
					<div class="fileArea clearfix">
						<div style="float:left;color:blue;line-height:20px;border:1px solid #FFFFFF;">附件：</div>
						<c:forEach items="${notice.files }" var="file">
							<a href="${ctx }/AttachmentController/getFile.do?fileId=${file.fileId }" class="fileItem">
								<b class="fileIcon"></b>
								${file.fileName }
								<span class="fileSize">(${file.fileSize })</span>
							</a>
						</c:forEach>
					</div>
					<p class="sign">
						${notice.staffName }
						<br />
						${notice.publishTime }
					</p>
				</div>
			</div>
			<!-- 回复区 -->
			<div class="reply-area">
				<div class="reply-title">
					<span class="holder"></span>
					<span style="margin-left:20px;line-height:40px;float:left;">共<b class="total">${total }</b>回复</span>
					<ul class="menu-group">
						<li>
							<a id="quickBtn" class="quick-reply" href="#edit">回复</a>
						</li>
					</ul>
				</div>
				<div class="reply-panel">
					<div class="reply-list" id="itemContainer">
						<c:forEach items="${replys }" var="reply" varStatus="status">
							<div class="reply-item">
								<div class="user-info clearfix">
									<div class="user-icon">
										<img src="${ctx}/res/theme-${uiInfo.theme }/images/notice/sex1.jpg" />
									</div>
									<div class="user-cont">
										<div class="firstline">
											<span class="name">${reply.userName }</span>
											<span class="org">机构&nbsp;${reply.orgName }</span>
											<span class="time">${status.index + 1 }楼 ${reply.publishTime }</span>
										</div>
										<%-- <div class="secondline">
											<span class="org">机构&nbsp;${reply.orgName }</span>
											<span class="time">${status.index + 1 }楼 ${reply.publishTime }</span>
										</div> --%>
									</div>
								</div>
								<div class="msg-info">${reply.replyContent }</div>
							</div>
						</c:forEach>
					</div>
					<div class="reply-edit clearfix">
						<!-- <a name="edit" style="display:none;"></a> -->
						<script id="myEditor" style="width:90%;height:300px;" type="text/plain"></script>
						<a name="edit" id="publishBtn" class="menu first">发&nbsp;表</a>
					</div>
				</div>
			</div>
		</div>
		<div id="msgDialog" class="msgDialog">
		</div>
		<div id="loadDialog" class="loadDialog">
			<b></b><span></span>
		</div>
	</body>
</html>