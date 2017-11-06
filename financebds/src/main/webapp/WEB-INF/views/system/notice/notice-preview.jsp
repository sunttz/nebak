<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>
			公告预览：
			<c:choose>
				<c:when test="${empty notice.noticeTitle }">
					无标题
				</c:when>
				<c:when test="${!empty notice.noticeTitle }">
					${notice.noticeTitle }
				</c:when>
			</c:choose>
		</title>
		<link type="text/css" rel="stylesheet" href="${ctx}/res/theme-${uiInfo.theme }/notice/notice-preview.css" />
	</head>
	<body>
		<div class="head">
			<div class="h_logo">
				<img width="400" height="80" src="${ctx }${uiInfo.loginlogo }">
			</div>
		</div>
		<div class="bd">
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
		</div>
	</body>
</html>