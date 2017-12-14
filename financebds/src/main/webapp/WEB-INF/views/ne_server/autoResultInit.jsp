<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML>
<html>
	<head>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		<script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${ctx }/res/js/ne_server/autoResultInit.js"></script>
		<script>
			var ctx='${ctx}';
		</script>
	</head>	
	
	<body class="easyui-layout">
		<div data-options="region:'north', border:false" style="height:130px;padding: 1px 1px 1px 0px;">
			<div id="searchPanel" class="easyui-panel" data-options="fit:true,title:'备份管理列表'" style="background:#F4F4F4;">
				<table style="height:100%;">
					<tr>
						<td align="right" style="width: 120px;">输入日期：</td>
						<td align="left"  style="width: 230px;">
							<input type="text" id="date_time" name="date_time" placeholder="格式:20160101">
						</td>
						<td style="padding-left: 20px;">
							<a id="job_log_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">搜索</a>
						</td>
					</tr>
					<tr>
						<td align="right" style="width: 120px;"></td>
						<td colspan="4">默认当前只取今天的，如果需要查询其他日期，请输入条件"输入日期"</td>
					</tr>
				</table>
			</div>
	    </div>
	    <div data-options="region:'center',border:false" style="padding:3px;">
			<table id="listTable"></table>
		</div>
	</body>
	
</html>	