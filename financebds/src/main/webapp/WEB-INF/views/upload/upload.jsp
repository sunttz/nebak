<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML>
<html>
	<head>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		<style type="text/css">
			.exp_btn {
			    background-color: blue;
			    color: #FFFFFF;
			    text-align: center;
			    height: 23px;
			    left: 340px;
			    position: absolute;
			    line-height:20px;
			    top: 10px;
			    width: 109px;
			}
			.file {
			    height: 23px;
			    left: 340px;
			    opacity: 0;
			    cursor: pointer;
			    position: absolute;
			    line-height:20px;
			    top: 10px;
			    z-index: 4;
			    width: 109px;
			}
			.inpWrap{margin-left:10px}
		</style>
	</head>
	
	<body>
	<form method="post" enctype="multipart/form-data" id="uploadForm" >
		<div class="box">
				<div class="inpWrap">
				    <input id="uploadFile" name="uploadFile" readonly type="text" style="width:300px;height:23px;line-height:20px;border:1px solid #C4C4C4;color:#555555;" />
				    <input id="logo" name="logo" type="file" class="file" onchange="document.getElementById('uploadFile').value=this.value;"/>
				    <a href="#"><span class="exp_btn">上传...</span></a>
				</div>
		</div>
	</form>
	</body>
</html>