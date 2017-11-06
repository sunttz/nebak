<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> 
<html> 
	<head> 
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">
		<title>404</title>
		<style type="text/css">
        body{padding:0; margin:0; font-family:"微软雅黑", "宋体"; font-size:12px;}
        #main{position:absolute; top:0px; right:0px; bottom:0px; left:0px; width:100%; height:100%;}
        .box1{position:relative;width:669px; height:436px; margin:10% auto 0px auto;}
        .box1 .text{position:absolute; bottom:80px; left:30%; width:100px; line-height:25px; font-size:14px;}
        .box1 .text a{color:#a31818; text-decoration:none;}
        .box1 .text a:hover{ text-decoration:underline;}
        </style>
        <script type="text/javascript">
//         window.onload=function(){
//          	document.getElementById("b").innerHTML='<a href="javascript:history.go(-1)">返回&gt;&gt;</a>';  	
//         };
        </script>
	</head> 
	<body> 
	<div class="box1">
   	  	<img src="${ctx}/res/outlook/web-404.jpg" width="669px" height="436px" alt=""/>
        <b class="text" id='b'></b>
	</div>
	</body> 
</html>