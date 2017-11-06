<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
	<meta name="renderer" content="webkit">
	<%--附件上传必须的样式文件 --%>
	<link rel="stylesheet" type="text/css" href="${ctx}/res/bootstrap-3.3.2/css/bootstrap.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/res/jQuery-File-Upload-9.9.3/css/jquery.fileupload.css">
	<link rel="stylesheet" type="text/css" href="${ctx}/res/jQuery-File-Upload-9.9.3/css/jquery.fileupload-ui.css">

	<%--附件上传必须的js文件 --%>
	<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="${ctx}/res/jQuery-File-Upload-9.9.3/js/vendor/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="${ctx}/res/jQuery-File-Upload-9.9.3/js/jquery.iframe-transport.js"></script>
	<script type="text/javascript" src="${ctx}/res/jQuery-File-Upload-9.9.3/js/jquery.fileupload.js"></script>
	<script type="text/javascript" src="${ctx}/res/javascript/attachment/myuploadfunction.js"></script>
	<title>附件上传</title>
</head>

<body>

<div style="width:100%;padding:10px">
	<%--<h3>附件上传管理</h3> --%>
	
	<%--get请求的两个参数，隐藏待提交--%>
	<input type="hidden" id="groupCode" name="groupCode" value='<c:out value="${groupCode}"/>' />
	<input type="hidden" id="relationId" name="relationId" value='<c:out value="${relationId}"/>' />
	
	<%--用下面的样式展现出一个按钮，掩盖默认的input type=file行为--%>
	<span class="btn btn-success fileinput-button">
		<span>上传</span>
		<%-- multiple是html5的特性，IE8及以下没有作用--%>
		<input id="fileupload" type="file" name="files[]" data-url="upload.do" multiple="multiple" style="width:90%">
	</span>
	
	<%--上传成功后，展示上传的文件信息的表格，样式采用bootstrap--%>
	<table id="uploaded-files" class="table table-hover">
		<tr>
			<th>文件名</th>
			<th>文件大小（Kb）</th>
			<th>文件类型</th>
			<th>操作</th>
		</tr>
		<%--迭代出对应的已上传的文件，c:if判断是否为空--%>
		<c:if test="${ !('' eq files) }">
			<c:forEach items="${files}" var="entity" varStatus="status">  
				<tr>
					<td  style="word-break:break-all;word-wrap:break-word">${entity.fileName}</td>
					<td>${entity.fileSize}</td>
					<td>${entity.fileType}</td>
					<td><a href="getFile.do?fileId=${entity.fileId}">下载</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="#" onclick="fileDel(${entity.fileId})">删除</a></td>
				</tr>
            </c:forEach>					
		</c:if> 		
	</table>
	
</div>
</body> 
</html>