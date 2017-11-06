<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
	<meta name="renderer" content="webkit">
	<script type="text/javascript" src="${ctx}/res/javascript/common/jquery-1.11.2.min.js"></script>
	<link rel="stylesheet" type="text/css" href="${ctx}/res/bootstrap-3.3.2/css/bootstrap.css">
	<title>附件下载</title>
</head>

<body>

<div style="width:100%;padding:10px">
	<h4>附件列表</h4>
	<table id="uploaded-files" class="table table-hover">
		<tr>
			<th>#</th>
			<th>文件名</th>
			<th>文件大小（Kb）</th>
			<th>文件类型</th>
			<th>下载次数</th>
			<th>操作</th>
		</tr>
		<c:if test="${ !('' eq files) }">
			<c:forEach items="${files}" var="entity" varStatus="status">  
				<tr>
					<td>${status.index+1}</td>
					<td  style="word-break:break-all;word-wrap:break-word">${entity.fileName}</td>
					<td>${entity.fileSize}</td>
					<td>${entity.fileType}</td>
					<td>${entity.downloadTimes}</td>
					<td><a href="getFile.do?fileId=${entity.fileId}" onclick="addTimes()">下载</a></td>
				</tr>
            </c:forEach>					
		</c:if>   

	</table>
	
</div>
</body> 
</html>
<script>

//自由控制IFRAME高度（要求父页面iframe的那么是listWindow）要防止异常
try{
	parent.document.all.listWindow.height=document.body.scrollHeight;
}catch(e){}

//增加下载次数
function addTimes(){
	//取得点击对象最近的td的前一个兄弟，也就是下载次数所在的单元格
	var prevTd = $(event.srcElement).closest("td").prev();
	//取得上面单元格对象的文本转换为数字+1
	var downloadTimes = parseInt($(prevTd).text())+1;
	//给下载次数单元格内容赋值
	$(prevTd).text(downloadTimes);
}
</script>