<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML>
<html>
	<head>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		<script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${ctx }/res/js/upload/downloadList.js"></script>
	</head>	
	
	<body class="easyui-layout">
		<div data-options="region:'north', border:false" style="height:130px;padding: 1px 1px 1px 0px;">
			<div id="searchPanel" class="easyui-panel" data-options="fit:true,title:'下载列表'" style="background:#F4F4F4;">
				<table style="height:100%;">
						<tr align="center">
							<td align="right" style="width: 160px;">真实名：</td>
							<td align="center"   colspan="2">
								<input type="text" id="realName" name="realName" style="width: 100%;text-align: center;" placeholder="关键字" />
							</td>
						</tr>
					<tr>
						<td align="right" style="width: 160px;">开始时间：</td>
						<td align="left"  style="width: 350px;">
							<input type="text" id="startTime" style="width:160px;" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})" class="Wdate" />
							-
							<input type="text" id="endTime" style="width:160px;" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'})" class="Wdate" />
						</td>
						<td style="padding-left: 20px;">
							<a id="job_log_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">搜索</a>
						</td>
					</tr>
				</table>
			</div>
	    </div>
	    <div data-options="region:'center',border:false" style="padding:3px;">
			<table id="listTable"></table>
		</div>
	</body>
	
</html>	