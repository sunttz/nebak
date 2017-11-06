var clearChecked = function(obj) {
	var rootNode = obj.tree('getRoot');
	obj.tree('uncheck', rootNode.target);
};
var roleRowData = null;

var loadRole = function() {
	//角色列表
	$('#roleTable').datagrid({
		url:'getRoles.do',
		method:'post',
		striped:true,
		fit:true,
		border:false,
		rownumbers:true,
		fitColumns:true,
		singleSelect:true,
		columns:[[
		    {field:'roleId',title:'角色ID',halign:'center',align:'left',width:40},
		    {field:'roleCode',title:'角色编码',halign:'center',align:'left',width:50},
			{field:'roleName',title:'角色名称',halign:'center',align:'left',width:50},
			{field:'roleDesc',title:'角色备注',halign:'center',align:'left',width:90,formatter:function(value, row, index) {
				var val = value==null?'':value;
				return '<div class="content" title="'+val+'">'+val+'</div>';
			}}
		]],
		onBeforeLoad:function(param) {
			//IE缓存
			param.ss = new Date().getTime();
			param.roleName = $('#roleName').val();
		},
		onSelect:function(rowIndex, rowData) {
			//记录当前选中的角色
			roleRowData = rowData;
			/**********************菜单树*************************/
			//勾选菜单树
			clearChecked($('#menuTree'));
			var ids = roleRowData.menuIds;
			$("#menu").html('【'+roleRowData.roleName+'】权限配置');
			for(var i = 0, n = ids.length; i < n; i++) {
				var node = $('#menuTree').tree('find', ids[i]);
				var childs= $('#menuTree').tree('getChildren',node.target);
				var count = childs.length;
				if(count==0) {
					$('#menuTree').tree('check', node.target);
				}
				
			}
			/**********************成员列表*************************/
			$('#roleStaff_userId, #roleStaff_operatorName').val('');
			$('#roleStaffTable').datagrid('options').url = 'getStaffs.do?roleId=' + roleRowData.roleId;
			$('#roleStaffTable').datagrid('load');
		}
	});
};

$(document).ready(function() {
	//菜单树
	//TODO 暂时简单处理：让树先加载完再加载角色
	$('#menuTree').tree({
		url:'getMenuTree.do',
		checkbox:true,
//		animate: true,
		onLoadSuccess:function(node,data) {
			var nodes = $('#menuTree').tree('getChecked', 'unchecked');
			for(var i=0;i<nodes.length;i++){
				if(nodes[i].isLeaf==0  & nodes[i].id!=0){
					$('#menuTree').tree('collapse',nodes[i].target);
				}
			}
		}
	});
	loadRole();
	
	$('#roleStaffTable').datagrid({
		url:'',
		method:'post',
		striped:true,
		fit:true,
		pagination:true,
		pageSize:10,
		border:false,
		rownumbers:true,
		fitColumns:true,
		singleSelect:false,
		selectOnCheck:true,
		checkOnSelect:true,
		columns:[[
		    {field:'ck',checkbox:true},
			{field:'userId',title:'登陆账号',halign:'center',align:'left',width:40},
			{field:'operatorName',title:'用户姓名',halign:'center',align:'left',width:40},
			{field:'orgName',title:'所属机构',halign:'center',align:'left',width:200,formatter: titleFormatter}
		]],
		onBeforeLoad:function(param) {
			//IE缓存
			param.ss = new Date().getTime();
			param.userId = $('#roleStaff_userId').val();
			param.operatorName = $('#roleStaff_operatorName').val();
		},
		onSelect:function(rowIndex, rowData) {
		},
		onLoadSuccess:function(data) {
			var row = $("#roleTable").datagrid('getSelected');
			if(row) {
				row.staffSize = data.total;
			}
		}
	}).datagrid('getPager').pagination({
		buttons: [{
			iconCls:'icon-add',
			text:'增加',
			handler:function(){
				if(roleRowData!=null) {
					$('#allStaffDialog').dialog('open');
				} else {
					$.messager.alert('提示','请选择您要操作的角色！','info');
				}
			}
		},'-',{
			iconCls:'icon-remove',
			text:'删除',
			handler:function(){
				var rows = $('#roleStaffTable').datagrid('getChecked');
				if(rows.length > 0) {
					$.messager.confirm('提示','是否确认删除？',function(r){
						if(r){
							var role = {
								roleId:roleRowData.roleId,
								staffIds:[]
							};
							for(var i = 0, n = rows.length; i < n; i++) {
								role.staffIds.push(rows[i].staffId);
							}
							$.ajax({
								'url': 'removeStaffs.do?ss='+new Date().getTime(),
						        'type': 'post',
						        'contentType': 'application/json;charset=UTF-8',
						        'data': JSON.stringify(role),
						        'success': function(data) {
						        	var message = null;
									if(data == 'success') {
										message = "保存成功！";
									} else {
										message = "系统异常，保存失败！";
									}
									$.messager.alert('提示',message,'info');
									$('#roleStaffTable').datagrid('load');
						        }
							});
						}
					});
				} else {
					$.messager.alert('提示','请选择您要删除的成员！','info');
				}
			}
		}]
	});
	
	$('#allStaffTable').datagrid({
		url:'',
		method:'post',
		striped:true,
		fit:true,
		pagination:true,
		pageSize:10,
		border:false,
		rownumbers:true,
		fitColumns:true,
		singleSelect:false,
		selectOnCheck:true,
		checkOnSelect:true,
		columns:[[
		     {field:'staffId',title:'ID',hidden:true},
		    {field:'ck',checkbox:true},
			{field:'userId',title:'登陆账号',halign:'center',align:'left',width:40},
			{field:'operatorName',title:'用户姓名',halign:'center',align:'left',width:40},
			{field:'orgName',title:'所属机构',halign:'center',align:'left',width:100,formatter: titleFormatter}
		]],
		onBeforeLoad:function(param) {
			//IE缓存
			param.ss = new Date().getTime();
			param.userId = $('#allStaff_userId').val();
			param.operatorName = $('#allStaff_operatorName').val();
		},
		onSelect:function(rowIndex, rowData) {
		},
		onLoadSuccess:function() {
		}
	}).datagrid('getPager').pagination({
		buttons: [{
			iconCls:'icon-save',
			text:'提交',
			handler:function(){
				var rows = $('#allStaffTable').datagrid('getChecked');
				if(rows.length > 0) {
					$.messager.confirm('提示','是否确认提交？',function(r){
						if(r){
							var role = {
								roleId:roleRowData.roleId,
								staffIds:[]
							};
							for(var i = 0, n = rows.length; i < n; i++) {
								role.staffIds.push(rows[i].staffId);
							}
							$.ajax({
								'url': 'postStaffs.do?ss='+new Date().getTime(),
						        'type': 'post',
						        'contentType': 'application/json;charset=UTF-8',
						        'data': JSON.stringify(role),
						        'success': function(data) {
						        	var message = null;
									if(data == 'success') {
										message = "保存成功！";
									} else {
										message = "系统异常，保存失败！";
									}
									$.messager.alert('提示',message,'info');
									$('#roleStaffTable').datagrid('load');
									$('#allStaffTable').datagrid('load');
						        }
							});
						}
					});
				} else {
					$.messager.alert('提示','请选择您要添加的成员！','info');
				}
			}
		}]
	});
	
	$('#allStaffDialog').dialog({
		title: '添加成员',
		//fit: true,
		width:800,
		height:400,
		closed: true,
		cache: false,
		draggable: false,
		modal: true,
		onOpen: function() {
			$('#allStaff_userId, #allStaff_operatorName').val('');
			$('#allStaffTable').datagrid('options').url = 'getAllOtherStaffs.do?roleId=' + roleRowData.roleId;
			$('#allStaffTable').datagrid('load');
		}
	});
	
	$('#roleStaff_searchBtn').click(function() {
		$('#roleStaffTable').datagrid('load');
	});
	
	$('#role_searchBtn').click(function() {
		$('#roleTable').datagrid('load');
	});
	
	$('#allStaff_searchBtn').click(function() {
		$('#allStaffTable').datagrid('load');
	});
	
	$('#roleDialog').dialog({
		title: '添加角色',
		iconCls: 'icon-save',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		onClose: function() {
			$("#roleForm").form('clear');
		}
	});
	
	$('#roleForm').form({
		url:'postRole.do',
		onSubmit:function(param) {
			var roleName = $.trim($('#roleForm input[name="roleName"]').val());
			var roleDesc = $.trim($('#roleForm textarea[name="roleDesc"]').val());
			var roleCode = $.trim($('#roleForm input[name="roleCode"]').val());
			$('#roleForm input[name="roleName"]').val(roleName);
			$('#roleForm textarea[name="roleDesc"]').val(roleDesc);
			$('#roleForm input[name="roleCode"]').val(roleCode);
			if(roleName.length==0) {
				$.messager.alert('提示','名称不能为空！','info');
				return false;
			}
			if(roleCode.length==0) {
				$.messager.alert('提示','角色编码不能为空！','info');
				return false;
			}
			
			if($('#roleForm input[name="roleId"]').val()=='') {
				$('#roleForm input[name="roleId"]').val('-1');
			}
		},
		success:function(data) {
			if(data == 'success'){
				$('#roleDialog').dialog('close');
				$.messager.alert('提示','保存成功！','info');
				$("#roleTable").datagrid('load');
			} else if(data == 'exist'){
				$.messager.alert('提示','角色编码已存在！','error');
			} else {
				$.messager.alert('提示','系统异常，保存失败！','error');
			}
		}
	});
	
	$('#saveMenusBtn').click(function() {
		var row = $("#roleTable").datagrid('getSelected');
		if(row) {
			$.messager.confirm('提示','是否确认提交修改？',function(r){
				if(r){
					var role = {
						roleId:row.roleId,
						menuIds:[]
					};

					var nodes_1 = $('#menuTree').tree('getChecked', 'checked');
					var nodes_2 = $('#menuTree').tree('getChecked', 'indeterminate'); 
					var nodes = nodes_1.concat(nodes_2);
					for(var i = 0, n = nodes.length; i < n; i++) {
						if(nodes[i].id != 0) {
							role.menuIds.push(nodes[i].id);
						}
					}
					$.ajax({
						'url': 'postMenus.do?ss='+new Date().getTime(),
				        'type': 'post',
				        'contentType': 'application/json;charset=UTF-8',
				        'data': JSON.stringify(role),
				        'success': function(data) {
				        	var message = null;
							if(data == 'success') {
								message = "保存成功！";
								row.menuIds = role.menuIds;
							} else {
								message = "系统异常，保存失败！";
							}
							$.messager.alert('提示',message,'info');
				        }
					});
				}
			});
		} else {
			$.messager.alert('提示','请选择一个角色！','info');
		}
		
	});
	
	$('#clearMenusBtn').click(function() {
		clearChecked($('#menuTree'));
		var ids = roleRowData.menuIds;
		for(var i = 0, n = ids.length; i < n; i++) {
			var node = $('#menuTree').tree('find', ids[i]);
			var childs= $('#menuTree').tree('getChildren',node.target);
			var count = childs.length;
			if(count==0) {
				$('#menuTree').tree('check', node.target);
			}
		}
	});
	
	$('#addRoleBtn').click(function() {
		$('#roleDialog').dialog({
			title:'添加角色'
		}).dialog('open');
	});
	
	$('#updateRoleBtn').click(function() {
		var row = $("#roleTable").datagrid('getSelected');
		if(row) {
			$('#roleDialog').dialog({
				title:'修改角色'
			}).dialog('open');
			$('#roleForm').form('load', row);
		} else {
			$.messager.alert('提示','请选择您要修改的角色！','info');
		}
	});
	
	$('#removeRoleBtn').click(function() {
		var row = $("#roleTable").datagrid('getSelected');
		if(row) {
			var staffSize = row.staffSize;
			if(staffSize > 0) {
				$.messager.alert('提示','该角色下还有' + staffSize + '个成员，无法进行删除操作！','info');
				return;
			}
			$.messager.confirm('提示','是否确认删除？',function(r){
				if(r){
					var roleId = row.roleId;
					//ajax进行删除
					$.ajax({
						'url': 'removeRole.do?ss=' + new Date().getTime(),
				        'type': 'post',
				        'data': {roleId:roleId},
				        'success': function(data) {
				        	var message = data=='success'?'保存成功！':'系统异常，保存失败！';
							$.messager.alert('提示',message,'info');
							$("#roleTable").datagrid('load');
				        }
					});
				}
			});
			
		} else {
			$.messager.alert('提示','请选择您要删除的角色！','info');
		}
	});
});
//保存角色
function saveUser(){
	$('#roleForm').submit();
	$('#roleName').val('');
	$('#roleTable').datagrid('load');
}
//字段较长的增加title显示
function titleFormatter(value, row, index){
	if(value) return "<div title='"+value+"'>"+value+"</div>";
	return "";
}