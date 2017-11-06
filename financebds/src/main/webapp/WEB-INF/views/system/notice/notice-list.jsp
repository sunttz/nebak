<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>系统公告</title>
		<link rel="stylesheet" type="text/css" href="${ctx }/res/theme-${uiInfo.theme }/notice/notice-list.css" >
	</head>
	<body>
		<div class="main">
			<div class="main-panel">
				<div class="title-bar">
					<h3>系统公告</h3>
				</div>
				<ul class="notice-list">
					<c:forEach items="${noticeItems }" var="item">
						<li class="notice-item">
							<a href="${ctx }/notice/browseNotice.do?noticeId=${item.noticeId }" target="_blank">
								<strong>
									<c:choose>
										<c:when test="${empty item.noticeTitle }">
											无标题
										</c:when>
										<c:when test="${!empty item.noticeTitle }">
											${item.noticeTitle }
										</c:when>
									</c:choose>
									<c:if test="${item.stickFlag eq 1}">
										<i title="置顶公告" class="icon_common stick"></i>
									</c:if>
								</strong>
								<c:choose>
									<c:when test="${item.fileNum > 0 }">
										<b title="此公告包含附件"></b>
									</c:when>
								</c:choose>
								<span class="read_more">
									${item.publishTime }
								</span>
							</a>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</body>
</html>