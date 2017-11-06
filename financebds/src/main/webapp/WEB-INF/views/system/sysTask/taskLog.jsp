<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
	<head>
	   	<title>任务配置</title>
	  	<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
	  	<script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
	  	<script type="text/javascript" src="${ctx }/res/javascript/system/sysTask/taskLog.js"></script>
	</head>
	<body class="easyui-layout">
		<div data-options="region:'north', border:false" style="height:56px;padding: 1px 1px 1px 0px;">
			<div id="searchPanel" class="easyui-panel" data-options="fit:true" style="background:#F4F4F4;">
				<table style="height:100%;">
					<tr align="center">
						<td align="right" style="width: 80px;">任务名称：</td>
						<td align="left"  style="width: 80px;">
							<input type="text" id="jobName" />
						</td>
						<td align="right" style="width: 80px;">开始时间：</td>
						<td align="left"  style="width: 350px;">
							<input type="text" id="startTime" style="width:160px;" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'endTime\')}'})" class="Wdate" />
							-
							<input type="text" id="endTime" style="width:160px;" 
								onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'startTime\')}'})" class="Wdate" />
						</td>
						<td style="padding-left: 20px;">
							<a id="job_log_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div data-options="region:'center',border:false" style="padding:3px;">
			<table id="logTable"></table>
		</div>
	</body>
</html>