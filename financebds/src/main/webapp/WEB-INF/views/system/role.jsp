<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
		<title>角色管理</title>
		<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
		
		<script type="text/javascript" src="${ctx }/res/javascript/system/role.js"></script>
		<style>
			html,body{overflow:hidden;height:100%;}
			.tail_bar{height:30px;border-top:1px solid #E6E6E6;background:#F4F4F4;}
			.top_bar{border-bottom:1px solid #DDDDDD;background:#F5F5F5;}
			.plain_button {float:left;margin:5px 0px 5px 5px;width:30px;height:18px;text-align:center;line-height:20px;cursor:pointer;border:1px solid #E0ECFF;color:#333333;}
			.plain_button:hover {border: 1px solid #95B8E7;}
			/* .datagrid-pager{background:#E0ECFF;border-color:#95B8E7;} */
			.content{overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}
			.red{color:red;}
		</style>
	</head>
	<body class="easyui-layout">
		<div data-options="region:'west',title:'角色列表',split:true" style="width:480px;">
			<div class="easyui-layout" data-options="fit:true">
				<div data-options="region:'north',border:false" class="top_bar" style="padding-left:10px;">
					<table style="height:100%;">
						<tr>
							<td style="padding-left: 5px;">角色名称：</td><td><input id="roleName" style="height:15px;line-height:15px" /></td>
							<td style="padding-left: 5px;"><a id="role_searchBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true"></a></td>
						</tr>
					</table>
				</div>
				<div data-options="region:'south',border:false" class="tail_bar">
					<a style="margin-top:2px;" id="addRoleBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true">添加</a>
					<a id="updateRoleBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true">修改</a>
					<a id="removeRoleBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true">删除</a>
				</div>
				<div data-options="region:'center',border:false">
					<table id="roleTable"></table>
				</div>
			</div>
		</div>
		<div data-options="region:'center'">
			<div class="easyui-tabs" data-options="fit:true,border:false">
				<div title="菜单权限配置">
					<div class="easyui-layout" data-options="fit:true">
						<div data-options="region:'south',border:false" class="tail_bar">
							<a id="saveMenusBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true">保存</a>
							<a id="clearMenusBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-undo',plain:true">重置</a>
						</div>
						<div data-options="region:'center',border:false" style="padding:10px;">
							<span id="menu"></span>
							<ul id="menuTree"></ul>
						</div>
					</div>
				</div>
				<div title="角色成员列表">
					<div class="easyui-layout" data-options="fit:true">
						<div data-options="region:'north',border:false" class="top_bar" style="padding-left:10px;">
							<table style="height:100%;">
								<tr>
									<td style="padding-left: 5px;">登陆账号：</td><td><input id="roleStaff_userId" style="height:15px;line-height:15px" /></td>
									<td style="padding-left: 5px;">用户姓名：</td><td><input id="roleStaff_operatorName" style="height:15px;line-height:15px" /></td>
									<td style="padding-left: 5px;"><a id="roleStaff_searchBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true"></a></td>
								</tr>
							</table>
						</div>
						<div data-options="region:'center',border:false">
							<table id="roleStaffTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="roleDialog" class="easyui-dialog" style="background-color:#F4F4F4; padding:10px 10px">
			<form id="roleForm" method="post">
				<input name="roleId" type="hidden" />
				<table>
					<tr style="height:40px">
						<td style="width:70px;" align="right"><span style="color:red;">*</span>角色名称：</td>
						<td style="width:260px;"><input name="roleName" style="height:20px;line-height:20px;width:238px;" /></td>
					</tr>
					<tr style="height:40px">
						<td style="width:70px;" align="right"><span style="color:red;">*</span>角色编码：</td>
						<td style="width:260px;"><input name="roleCode" style="height:20px;line-height:20px;width:238px;" /></td>
					</tr>
					<tr style="height:80px" valign="top">
						<td align="right">角色备注：</td>
						<td style="height:80px">
							<textarea name="roleDesc" style="width:240px;height:100%;border:1px solid #7F9DB9;overflow:auto;resize:none;"></textarea>
						</td>
					</tr>
					<tr style="height:50px">
						<td align="center" colspan="2">
							<a onclick="saveUser();" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">保存</a>
							<a onclick="$('#roleDialog').dialog('close');" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'">取消</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div style="display:none;">
			<div id="allStaffDialog"  style="padding:3px;">
				<div class="easyui-panel" data-options="fit:true,border:true">
					<div class="easyui-layout" data-options="fit:true">
						<div data-options="region:'north',border:false" class="top_bar" style="padding-left:10px;height:40px;">
							<table style="height:100%;">
								<tr>
									<td style="padding-left: 5px;">登陆账号：</td><td><input id="allStaff_userId" style="height:15px;line-height:15px" /></td>
									<td style="padding-left: 5px;">用户姓名：</td><td><input id="allStaff_operatorName" style="height:15px;line-height:15px" /></td>
									<td style="padding-left: 5px;"><a id="allStaff_searchBtn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true"></a></td>
								</tr>
							</table>
						</div>
						<div data-options="region:'center',border:false">
							<table id="allStaffTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>