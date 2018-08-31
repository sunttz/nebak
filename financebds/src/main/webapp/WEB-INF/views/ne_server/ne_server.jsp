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
		<div data-options="region:'north', border:false" style="height:115px;padding: 1px 1px 1px 0px;">
			<div id="searchPanel" class="easyui-panel" data-options="fit:true,title:'备份管理列表'" style="background:#F4F4F4;">
				<table style="height:100%;">
					<tr>
						<td align="right" style="width: 100px;">所属地区：</td>
						<td align="left"  style="width: 200px;">
							<input type="text" id="org_id" name="org_id">
						</td>
						<td align="right" style="width: 100px;">网元类型：</td>
						<td align="left"  style="width: 200px;">
							<select id="device_type" class="easyui-combobox" name="device_type" style="width:130px;">
								<option value="-1" selected>全部</option>
								<option value="MSC SERVER">MSC SERVER</option>
								<option value="MGW">MGW</option>
								<option value="HSS">HSS</option>
								<option value="SS">SS</option>
								<option value="STP">STP</option>
								<option value="DRA">DRA</option>
								<option value="MME">MME</option>
								<option value="SAE-GW">SAE-GW</option>
								<option value="PCRF">PCRF</option>
								<option value="DNS">DNS</option>
								<option value="VoLTE SBC">VoLTE SBC</option>
								<option value="CSCF">CSCF</option>
								<option value="VoLTE AS">VoLTE AS</option>
								<option value="ENS">ENS</option>
								<option value="IMSCSCF">IMSCSCF</option>
								<option value="IMSHSS">IMSHSS</option>
								<option value="CG">CG</option>
							</select>
						</td>
						<td align="right" style="width: 100px;">设备名称：</td>
						<td align="left"  style="width: 200px;">
							<input type="text" id="device_name" name="device_name">
						</td>
						<td style="padding-left: 20px;">
						</td>
					</tr>
					<tr>
						<td align="right" style="width: 100px;">备份类型：</td>
						<td align="left"  style="width: 200px;">
							<select id="bak_type" class="easyui-combobox" name="bak_type" style="width:130px;">
								<option value="" selected>全部</option>
								<option value="0">被动取</option>
								<option value="1">主动推</option>
							</select>
						</td>
						<td align="right" style="width: 100px;">保存类型：</td>
						<td align="left"  style="width: 200px;">
							<select id="save_type" class="easyui-combobox" name="save_type" style="width:130px;">
								<option value="" selected>全部</option>
								<option value="D">按天</option>
								<option value="W">按周</option>
							</select>
						</td>
						<td align="right" style="width: 100px;">保存份数：</td>
						<td align="left"  style="width: 200px;">
							<input type="text" id="save_day" name="save_day" onkeyup="value=value.replace(/[^\d]/g,'')" placeholder="只能输入数字">
						</td>
						<td style="padding-left: 20px;width: 85px;">
							<a id="job_log_btn" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'">搜索</a>
						</td>
					</tr>
					<tr>
						<td align="right" style="width: 120px;"></td>
						<td colspan="7">${diskinfo}</td>
					</tr>
				</table>
			</div>
	    </div>
	    <div data-options="region:'center',border:false" style="padding:3px;">
			<table id="listTable"></table>
		</div>
	</body>
	<div style="display:none;">
		<div id="progressbarDialog" style="padding:5px;">
			<div>网元备份中，请稍等。。。</div>
			<div>正在备份文件：<span id="downloading"></span></div>
		</div>
	</div>

</html>	