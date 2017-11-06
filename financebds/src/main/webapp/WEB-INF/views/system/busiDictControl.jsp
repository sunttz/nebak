<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>业务字典管理</title>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		
		<script type="text/javascript" src="${ctx }/res/javascript/system/busiDictControl.js"></script>
		<style>
			html,body{overflow:hidden;height:100%;}
			.tail_bar{height:30px;border-top:1px solid #E6E6E6;background:#F4F4F4;}
			.top_bar{height:30px;border-bottom:1px solid #95B8E7;background:#F5F5F5;}
			.plain_button {float:left;margin:5px 0px 5px 5px;width:30px;height:18px;text-align:center;line-height:20px;cursor:pointer;border:1px solid #E0ECFF;color:#333333;}
			.plain_button:hover {border: 1px solid #95B8E7;}
			/* .datagrid-pager{background:#E0ECFF;border-color:#95B8E7;} */
			.content{overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}
			.red{color:red;}
			.inp{width:250px;height:18px;}
		</style>
	</head>
	<body class="easyui-layout">
		<div data-options="region:'center',title:'业务场景'">
			<!-- <div id="searchPanel" style="background:#F4F4F4;">
				<table>
					<tr align="center" class="top_bar">
						<td align="right" style="width: 80px;">场景名称：</td>
						<td align="left"  style="width: 80px;"><input id="busiSceneName" /></td>
						<td align="right" style="width: 80px;">场景编码：</td>
						<td align="left"  style="width: 80px;"><input id="busiSceneCode" /></td>
						<td style="padding-left: 20px;"><a id="searchBut" href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search">查询</a></td>
					</tr>
				</table>
			</div>  -->
			<table id="sysSecne"></table>
		</div>
		<div data-options="region:'east',title:'业务字典',split:'true'" style="width:400px;">
			<div class="easyui-layout" data-options="fit:true">
				<div data-options="region:'center',border:false">
					<table id="busiDict"></table>
				</div>
				<div data-options="region:'south',border:false" class="tail_bar">
					<a style="margin-top:2px;"  href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addBusiDict();">增加</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" onclick="editBusiDict();">修改</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" onclick="deleteBusiDict();">删除</a>
				</div>
			</div>
		</div>
		
		<div style="display:none;">
			<div id="dlg">
				<form id="fm" method="post">
					<table style="width:350px;margin:20px 10px 0 10px;">
						<tr style="height:30px">
							<td style="width:100px;" align="right"><label><span style="color:red;">*</span>场景名称：</label></td>
							<td style="white-space: nowrap;">
								<input id="busiSceneNameId" name="busiSceneName" class="inp" />
							</td>
						</tr>
						<tr style="height:30px">
							<td align="right"><label><span style="color:red;">*</span>场景编码：</label></td>
							<td style="white-space: nowrap;">
								<input id="busiSceneCodeId" name="busiSceneCode" class="inp" />
							</td>
						</tr>
					<!-- 	<tr style="height:30px">
							<td align="right"><label>显示顺序：</label></td>
							<td>
								<input id="displayOrderId" name="displayOrder" class="inp" />
							</td>
						</tr> -->
						<tr valign="top">
							<td align="right" style="padding-top:10px;"><label>功能描述：</label></td>
							<td style="padding-top:7px;">
								<textarea id="sceneDescId" name="sceneDesc" style="width:250px;height:100px;overflow:auto;resize:none;"></textarea>
							</td>
						</tr>
					</table>
				</form>
				<div style="margin-top:24px;margin-bottom:10px;height:30px;text-align:center;">
					<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveSysScene();">保存</a>&nbsp;&nbsp;
					<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" onclick="javascript:$('#dlg').dialog('close');">取消</a>
				</div>
			</div>
		</div>
		<div style="display:none;">
			<div id="dlgB">
				<form id="fmB" method="post">
					<table style="width:400px;margin:10px 10px 0 10px;">
						<tr style="height:30px">
							<td style="width:100px;" align="right"><label><span style="color:red;">*</span>场景编码：</label></td>
							<td>
								<input id="busiSceneCodeIdB" name="busiSceneCode" class="inp" readonly="readonly" />
							</td>
						</tr>
						<tr style="height:30px">
							<td align="right"><label><span style="color:red;">*</span>字典名称：</label></td>
							<td>
								<input id="dicNameId" name="dicName" class="inp" />
							</td>
						</tr>
						<tr style="height:30px">
							<td align="right"><label><span style="color:red;">*</span>字典编码：</label></td>
							<td>
								<input id="dicCodeId" name="dicCode" class="inp" />
							</td>
						</tr>
						<tr valign="top">
							<td align="right" style="padding-top:10px;"><label><span style="color:red;">*</span>显示顺序：</label></td>
							<td style="padding-top:7px;">
								<input id="displayOrderIdB" name="displayOrder" class="inp" />
							</td>
						</tr>
					</table>
				</form>
				<div style="margin-top:24px;margin-bottom:10px;height:30px;text-align:center;">
					<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveBusiDictData();">保存</a>&nbsp;&nbsp;
					<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" onclick="javascript:$('#dlgB').dialog('close');">取消</a>
				</div>
			</div>
		</div>
		<div style="display:none;">
			<div id="tb" style="padding:5px;height:auto">
				<table style="height:30px;">
					<tr>
						<td style="padding-left:5px;">场景名称：</td>
						<td style="padding-left:5px;"><input id="busiSceneName" style="height:15px;line-height:15px" /></td>
						<td style="padding-left:5px;">场景编码：</td>
						<td style="padding-left:5px;"><input id="busiSceneCode" style="height:15px;line-height:15px" /></td>
						<td style="padding-left:15px;"><a id="searchBut" href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search">查询</a></td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>