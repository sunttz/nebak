<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<%-- 如果是360浏览器使用webkit内核，其他浏览器无视此标签 --%>
		<meta name="renderer" content="webkit">	
   		<title>菜单管理</title>
    	<%@ include file="/WEB-INF/views/common/taglibs.jsp"%>
    	<script type="text/javascript" src="${ctx }/res/javascript/system/menu.js"></script>
    	<style>
			html,body{overflow:hidden;height:100%;}
			.tail_bar{height:30px;border-top:1px solid #95B8E7;background:#E0ECFF;}
			.top_bar{height:30px;border-bottom:1px solid #95B8E7;background:#F5F5F5;}
			.plain_button {float:left;margin:5px 0px 5px 5px;width:30px;height:18px;text-align:center;line-height:20px;cursor:pointer;border:1px solid #E0ECFF;color:#333333;}
			.plain_button:hover {border: 1px solid #95B8E7;}
			.datagrid-pager{background:#E0ECFF;border-color:#95B8E7;}
			.content{overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}
			.red{color:red;}
			.info_table{border-collapse:collapse;border-spacing:0;}
			.info_table td{padding:0 15px;border:1px solid #DCDCDC;}
			.info_table td input{height:18px;line-height:18px;}
			.inp{width:100%;}
		</style>
	</head>
    <body class="easyui-layout">
    	<div data-options="region:'west',title:'菜单树',split:true" style="width:280px;">
	    	<ul id="menuTree" class="easyui-tree" ></ul>
		</div>
	    <div data-options="region:'center'">
	    	<div class="easyui-tabs" data-options="fit:true,border:false" id="menuTab">
	    		<div title="节点信息" style="padding:10px;">
	    			<div>
	    				<form id="menuUpdate" method="post" novalidate>
		    				<input type="hidden" name="menuId">
		    				<input type="hidden" name="menuSeq">
		    				<input type="hidden" name="parentId">
							<table class="info_table" style="width:100%;margin:0 auto;border:1px solid #DCDCDC;">
								<tr style="height:35px;">
									<td align="right" style="width:20%; background:#FAFAFA;"><span style="color:red;">*</span>菜单名称&nbsp;&nbsp;</td>
									<td>
										<input id="menuName" style="width:98%" type="text" name="menuName" />
									</td>
								</tr>
								<tr style="height:35px;">
									<td align="right" style="width:20%; background:#FAFAFA;">父菜单名称&nbsp;&nbsp;</td>
									<td>
										<input id="parentName" style="width:98%" type="text" name="parentName" disabled="disabled"/>
									</td>
								</tr>
								<tr style="height:35px;">
									<td align="right" style="width:20%; background:#FAFAFA;">
										<span style="color:red;">*</span>显示顺序&nbsp;&nbsp;
									</td>
									<td>
										<input id="displayOrder" style="width:98%" class="easyui-numberbox easyui-tooltip" title="最多为三位数" data-options="position: 'left'" name="displayOrder" type="text" />
									</td>
								</tr>
								<tr style="height:35px;">
									<td align="right" style="width:20%; background:#FAFAFA;"><span style="color:red;" id="isLfStar">*</span>是否叶子节点&nbsp;&nbsp;</td>
									<td>
										<input id="y" name="isLeaf" type="radio" value="1" style="margin-left:5px;"  onclick="checkAction();"/><label for="y">是</label>
										<input id="n" name="isLeaf" type="radio" value="0" style="margin-left:15px;" onclick="checkAction();"/><label for="n">否</label>
									</td>
								</tr>
								<tr style="height:35px;">
									<td align="right" style="width:20%; background:#FAFAFA;">菜单动作&nbsp;&nbsp;</td>
									<td>
										<textarea name="menuAction" id="menuAction" style="width:98%" rows="3"></textarea>
									</td>
								</tr>
								<tr style="height:60px;">
									<td align="right" style="width:20%; background:#FAFAFA;">菜单备注&nbsp;&nbsp;</td>
									<td>
										<textarea name="menuMemo" id="menuMemo" style="width:98%" rows="5"></textarea>
									</td>
								</tr>
							</table>
						</form>
					</div>
					<div style="text-align:center;padding-top:5px;">
						<input id="saveButton" style="width:60px;" type="button" value="保存" onclick="saveUser();" />&nbsp;&nbsp;&nbsp;&nbsp;
						<input id="resetButton" style="width:60px;" type="button" value="重置" onclick="clearForm();" />
					</div>
	    		</div>
	    	</div>
	    </div>
	    <div id="mm" class="easyui-menu" style="width:120px;">
		    <div onclick="reload();" data-options="iconCls:'icon-reload'">刷新</div>
		    <div onclick="append()" data-options="iconCls:'icon-add'">增加子菜单</div>
		    <div onclick="removeit()" data-options="iconCls:'icon-remove'">删除</div>
	    </div>
	    <div id="mm1" class="easyui-menu" style="width:120px;">
	    	<div onclick="reload();" data-options="iconCls:'icon-reload'">刷新</div>
		    <div onclick="removeit()" data-options="iconCls:'icon-remove'">删除菜单</div>
	    </div>
	    <div id="mm2" class="easyui-menu" style="width:120px;">
	    	<div onclick="reload();" data-options="iconCls:'icon-reload'">刷新</div>
		    <div onclick="append()" data-options="iconCls:'icon-add'">增加子菜单</div>
	    </div>
    </body>
    </html>