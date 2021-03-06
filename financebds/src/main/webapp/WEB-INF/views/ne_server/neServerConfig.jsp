<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML>
<html>
	<head>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		<script>
			var ctx='${ctx}';
		</script>
		<!-- My97DatePicker -->
		<script type="text/javascript" src="${ctx }/res/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript" src="${ctx }/res/js/ne_server/neServerConfig.js"></script>
		<style type="text/css">
			.info_table{border-collapse:collapse;border-spacing:0;}
			.info_table td{padding:0 15px;border:1px solid #DCDCDC;}
			.input{height:20px;line-height:15px;font-size:12px;}
			.validate_box{color:#b11516;margin-left:5px;display:none;}
			.upload{
				padding: 7px 10px;
				height: 20px;
				line-height: 20px;
				position: relative;
				border: 1px solid #999;
				text-decoration: none;
				color: #666;
			}
			.change{
				width: 0px;
				position: absolute;
				overflow: hidden;
				right: 0;
				top: 0;
				opacity: 0;
			}
			.button {
				background-color: #63B8FF;
				border: none;
				color: white;
				text-align: center;
				text-decoration: none;
				display: inline-block;
				font-size: 14px;
				margin: 4px 2px;
				cursor: pointer;
				-webkit-transition-duration: 0.4s; /* Safari */
				transition-duration: 0.4s;
			}
			.button2:hover {
				box-shadow: 0 12px 16px 0 rgba(0,0,0,0.24),0 17px 50px 0 rgba(0,0,0,0.19);
			}
		</style>
	</head>	
	
	<body class="easyui-layout">
		<div data-options="region:'north', border:false" style="height:90px;padding: 1px 1px 1px 0px;">
			<div id="searchPanel" class="easyui-panel" data-options="fit:true,title:'备份配置列表'" style="background:#F4F4F4;">
				<table style="height:100%;">
					<tr>
						<td align="right" style="width: 80px;">所属地区：</td>
						<td align="left"  style="width: 160px;">
							<input type="text" id="org_id" name="org_id">
						</td>
						<td align="right" style="width: 80px;">网元类型：</td>
						<td align="left"  style="width: 160px;">
							<input type="text" id="device_type" name="device_type">
							<!--
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
								<option value="MGCF">MGCF</option>
								<option value="CE">CE</option>
							</select>
							-->
						</td>
						<td align="right" style="width: 80px;">设备名称：</td>
						<td align="left"  style="width: 160px;">
							<input type="text" id="device_name" name="device_name">
						</td>
						<td align="right" style="width: 80px;">创建时间：</td>
						<td align="left"  style="width: 160px;">
							<input type="text" id="create_date" name="create_date" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});">
						</td>
					</tr>
					<tr>
						<td align="right" style="width: 80px;">备份类型：</td>
						<td align="left"  style="width: 160px;">
							<select id="bak_type" class="easyui-combobox" name="bak_type" style="width:130px;">
								<option value="" selected>全部</option>
								<option value="0">被动取</option>
								<option value="1">主动推</option>
							</select>
						</td>
						<td align="right" style="width: 80px;">保存类型：</td>
						<td align="left"  style="width: 160px;">
							<select id="save_type" class="easyui-combobox" name="save_type" style="width:130px;">
								<option value="" selected>全部</option>
								<option value="D">按天</option>
								<option value="W">按周</option>
							</select>
						</td>
						<td align="right" style="width: 80px;">保存份数：</td>
						<td align="left"  style="width: 160px;">
							<input type="text" id="save_day" name="save_day" onkeyup="value=value.replace(/[^\d]/g,'')" placeholder="只能输入数字">
						</td>
						<td colspan="2" style="padding-left: 20px;width: 80px;">
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

	<div style="display:none;">
		<div id="neServerDialog" style="padding:5px;">
			<form id="neServerForm" method="post" style="height: 300px;">
				<input name="serverId" id="serverId" type="text" style="display: none;" />
				<table style="width:100%;" class="info_table">
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>所属地区</td>
						<td id="orgId_box">
							<input type="text" id="orgId" name="orgId" style="width:425px;">
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>设备名称</td>
						<td id="deviceName_box">
							<input id="deviceName" name="deviceName"  style="width:420px;" class="input" size="30" />
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>网元类型</td>
						<td id="deviceType_box">
							<!--
							<select id="deviceType" class="easyui-combobox" name="deviceType" style="width:425px;">
								<option value=""></option>
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
								<option value="MGCF">MGCF</option>
								<option value="CE">CE</option>
							</select>
							-->
							<input type="text" id="deviceType" name="deviceType" style="width:425px;">
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>所属厂家</td>
						<td id="firms_box">
							<input type="text" id="firms" name="firms" style="width:425px;">
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>备份类型</td>
						<td id="bakType_box">
							<input id="bakType1" name="bakType" type="radio" value="0" style="margin-left:10px;vertical-align:middle;" checked="checked" />
							<label for="bakType1" style="vertical-align:middle;display:inline-block;">被动取</label>
							<input id="bakType2" name="bakType" type="radio" value="1" style="margin-left:15px;vertical-align:middle;" />
							<label for="bakType2" style="vertical-align:middle;display:inline-block;">主动推</label>
						</td>
					</tr>
					<!--
					<tr id="moduleTypeTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>模块类型</td>
						<td id="moduleType_box">
							<input id="moduleType1" name="moduleType" type="radio" value="single" style="margin-left:10px;vertical-align:middle;" checked="checked" />
							<label for="moduleType1" style="vertical-align:middle;display:inline-block;">单模块</label>
							<input id="moduleType2" name="moduleType" type="radio" value="multi" style="margin-left:15px;vertical-align:middle;" />
							<label for="moduleType2" style="vertical-align:middle;display:inline-block;">多模块</label>
						</td>
					</tr>
					-->
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>保存类型</td>
						<td id="saveType_box">
							<input id="saveType1" name="saveType" type="radio" value="D" style="margin-left:10px;vertical-align:middle;" checked="checked" />
							<label for="saveType1" style="vertical-align:middle;display:inline-block;">按天</label>
							<input id="saveType2" name="saveType" type="radio" value="W" style="margin-left:25px;vertical-align:middle;" />
							<label for="saveType2" style="vertical-align:middle;display:inline-block;">按周</label>
						</td>
					</tr>
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>保存份数</td>
						<td id="saveDay_box">
							<input id="saveDay" name="saveDay" style="width:420px;" class="input" size="30" value="7"/>
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>备注</td>
						<td id="remarks_box">
							<input id="remarks" name="remarks"  style="width:420px;" class="input" size="30" />
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<!--
					<tr id="bakPathTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>备份路径</td>
						<td id="bakPath_box">
							<input id="bakPath" name="bakPath" style="width:420px;" class="input" size="40"/>
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					-->
					<tr id="bakUserdataTr" style="height:30px;white-space: nowrap;display: none;">
						<td style="width:16%;background:#FAFAFA;" >用户数据路径</td>
						<td id="bakUserdata_box">
							<input id="bakUserdata" name="bakUserdata" style="width:420px;" class="input" size="40"/>
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr id="bakSystemTr" style="height:30px;white-space: nowrap;display: none;">
						<td style="width:16%;background:#FAFAFA;" >系统数据路径</td>
						<td id="bakSystem_box">
							<input id="bakSystem" name="bakSystem" style="width:420px;" class="input" size="40"/>
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<!--
					<tr id="deviceAddrTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>设备地址</td>
						<td id="deviceAddr_box">
							<input id="deviceAddr" name="deviceAddr" style="width:420px;" class="input" size="40" />
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr id="devicePortTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>设备端口</td>
						<td id="devicePort_box">
							<input id="devicePort" name="devicePort" style="width:420px;" onkeyup="value=value.replace(/[^\d]/g,'')" class="input" size="40" value="21"/>
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr id="userNameTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>用户名</td>
						<td id="userName_box">
							<input id="userName" name="userName" style="width:420px;" class="input" size="30" />
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					<tr id="passWordTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>密码</td>
						<td id="passWord_box">
							<input id="passWord" name="passWord"  style="width:420px;" class="input" size="30" />
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
					-->
					<tr id="moduleListTr" style="height:30px;white-space: nowrap;">
						<td style="width:16%;background:#FAFAFA;" ><span style="color:red;">*</span>模块列表</td>
						<td id="moduleList_box">
							<a id="moduleList" href="#" onclick="editModule();" style="margin-left: 10px;text-decoration: none;">查 看（<span id="moduleNum">0</span>）</a>
							<input id="addOrUpdate" name="addOrUpdate" type="hidden" value="add">
							<input id="neServerModuleId" name="neServerModuleId" type="hidden" value="">
							<span class="validate_box">
								<span style="margin-right:5px;">●</span><span class="validate_msg"></span>
							</span>
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div style="text-align:center;padding-top:10px;" id="add_btns">
			<a href="javascript:void(0)" style="margin-right: 10px;" class="easyui-linkbutton"
			   data-options="iconCls:'icon-save',plain:true" onclick="saveNeServer()">保存</a>
			<a href="javascript:void(0)" style="margin-left: 10px;" class="easyui-linkbutton"
			   data-options="iconCls:'icon-cancel',plain:true" onclick="$('#neServerDialog').dialog('close');">取消</a>
		</div>
	</div>

	<div id="importFile" class="easyui-dialog" title="导入数据" style="width:400px;height:230px;padding:10px"
		 data-options="iconCls: 'icon-import',buttons: '#importFile-buttons',closed:true,modal: true">
		<div style="height: 50px;width: 100%;text-align: center;">
			<form id="excelForm"  method="post" >
				<a href="javascript:;" class="upload">
					<span onclick="javascript:$('#excelFile').click();">选择文件</span><input type="file" class="change" id="excelFile" name="excelFile" accept=".xls,.xlsx" onchange="document.getElementById('filename').value=this.files[0].name"/>
					<input type="text" id="filename" readonly="readonly" onclick="javascript:$('#excelFile').click();">
					<input id="importFileBtn" type="button" class="button button2" onclick="doUpload()" value="导  入" />
				</a>
			</form>
		</div>
		<div style="height: 60px;width: 100%;text-align: center;">
			<hr/>
			<span id="importing">请选择导入文件</span>
			<div id="importResult" style="display: none;">
				<span style="display: block;">被动取类型导入成功&nbsp;<span id="successNum_get" style="font-weight: bold">0</span>&nbsp;条，失败&nbsp;<span id="failNum_get" style="font-weight: bold">0</span>&nbsp;条。</span>
				<span style="display: block;">主动推类型导入成功&nbsp;<span id="successNum_put" style="font-weight: bold">0</span>&nbsp;条，失败&nbsp;<span id="failNum_put" style="font-weight: bold">0</span>&nbsp;条。</span>
				<span style="display: block;"><a href="#" onclick="javascript:getImportDataToday();">查看当天导入数据</a></span>
			</div>
		</div>
		<div></div>
	</div>
	<div id="importFile-buttons">
		<span style="float: left;">仅允许导入"xls"或"xlsx"格式文件！</span>
		<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#importFile').dialog('close')">关闭</a>
	</div>

</html>	