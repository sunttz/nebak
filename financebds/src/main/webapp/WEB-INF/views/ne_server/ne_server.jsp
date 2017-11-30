<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML>
<html>
	<head>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		<script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${ctx }/res/js/ne_server/neServer.js"></script>
		<script>
			var ctx='${ctx}';
		</script>
	</head>	
	
	<body class="easyui-layout">
		<div data-options="region:'north', border:false" style="height:105px;padding: 1px 1px 1px 0px;">
			<div id="searchPanel" class="easyui-panel" data-options="fit:true,title:'备份管理列表'" style="background:#F4F4F4;">
				<table style="height:100%;">
					<tr>
						<td align="right" style="width: 120px;">所属地区：</td>
						<td align="left"  style="width: 230px;">
							<input type="text" id="org_id" name="org_id">
						</td>
						<td align="right" style="width: 120px;">网元类型：</td>
						<td align="left"  style="width: 230px;">
							<select id="device_type" class="easyui-combobox" name="device_type" style="width:150px;">
							    <option value="-1" selected>全部</option>
							    <option value="SERVER">SERVER</option>
							    <option value="MGW">MGW</option>
							</select>
						</td>
						<td style="padding-left: 20px;">
							<a id="job_log_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">搜索</a>
						</td>
					</tr>
					<tr>
						<td align="right" style="width: 120px;"></td>
						<td colspan="4">${diskinfo}</td>
					</tr>
				</table>
			</div>
	    </div>
	    <div data-options="region:'center',border:false" style="padding:3px;">
			<table id="listTable"></table>
		</div>
	</body>
	
</html>	