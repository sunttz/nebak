/**
 * @author long.ming
 * 创建时间：2014-3-26 下午2:56:48
 * 
 * 说明：
 * 1.页面加载时，默认选中第一行，根据选中场景查询业务字典
 * 2.场景新增保存时，调用ajax查询数据库是否有场景编码相同的编码（有：不可新增；无：可新增），同时有非空判断，数字校验
 * 3.场景修改时，默认场景编码为只读不可修改（新增时将只读属性删除），同时有非空判断，数字校验
 * 4.场景删除时，调用ajax查询该场景下是否有业务字典（有：不可删；无：可删）
 * 5.字典新增保存时，默认根据选择场景带出场景编码且只读，
 * 	 同时调用ajax根据场景编码、字典编码查询字典表是否有相同的字典编码（有：不可新增；无：可新增），同时有非空判断，数字校验
 * 6.字典修改时，默认场景编码为只读不可修改，同时有非空判断，数字校验
 * 7.字典删除时：可直接删除
 * Todo:将表单中的输入内容前后去空保存
 */
var url;

$(document).ready(function() {
	/******************************业务场景表格始化******************************/
	$('#sysSecne').datagrid({
		url:'querySysSceneControl.do',
		pagination:true,
		pageSize:10,
		striped:true,
		rownumbers:true,
		fit:true,
		border:false,
		fitColumns:true,
		singleSelect:true,
		toolbar:'#tb',
		columns:[[
		        {field:'busiSceneName',title:'场景名称',halign:'center',align:'left',width:50},
		        {field:'busiSceneCode',title:'场景编码',halign:'center',align:'left',width:50},
		        {field:'sceneDesc',title:'场景说明',halign:'center',align:'left',width:80}
		]],
		onSelect: function(rowIndex, rowData){
			querySysDic(rowData.busiSceneCode);
		},
		onLoadSuccess: function() {
			$('#sysSecne').datagrid('selectRow', 0);
		}
	}).datagrid('getPager').pagination({
		buttons: [{
			iconCls:'icon-add',
			text:'增加',
			handler:function(){
				$("[id='busiSceneCodeId']").removeAttr("readonly");
				addSysScene();
			}
		},'-',{
			iconCls:'icon-edit',
			text:'修改',
			handler:function(){
				$("[id='busiSceneCodeId']").attr({readonly:"readonly"});
				editSysScene();
			}
		},'-',{
			iconCls:'icon-remove',
			text:'删除',
			handler:function(){
				deleteSysScene();
			}
		}]
	});
	/******************************业务字典表格始化******************************/
	$('#busiDict').datagrid({
		url:'',
		striped:true,
		rownumbers:true,
		fit:true,
		border:false,
		fitColumns:true,
		singleSelect:true,
		columns:[[
			    {field:'dicName',title:'字典名称',halign:'center',align:'left',width:50},
		        {field:'dicCode',title:'字典编码',halign:'center',align:'left',width:50}
		]]
	});
	//业务场景查询按钮
	$('#searchBut').click(function() {
		var busiSceneName = $('#busiSceneName').val().replace(/\s+/g,""); 
		var busiSceneCode = $('#busiSceneCode').val().replace(/\s+/g,""); 
		$('#sysSecne').datagrid('load', {
			busiSceneName : busiSceneName,
			busiSceneCode: busiSceneCode
		});
	});
	
	$('#dlg').dialog({
		title: '添加信息',
		width: 450,
		height: 'auto',
		iconCls: 'icon-edit',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		onClose: function() {
			$('#fm').form('clear');
		}
	});

	$('#dlgB').dialog({
		title: '添加信息',
		width: 450,
		height: 'auto',
		iconCls: 'icon-save',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		onClose: function() {
			$('#fmB').form('clear');
		}
	});
	
});
/******************************业务场景******************************/
//业务场景新增
function addSysScene(){
	$('#dlg').dialog('open').dialog('setTitle','增加信息');
	url = 'insertSysSceneControl.do';
}
//业务场景修改
function editSysScene(){
	var row = $('#sysSecne').datagrid('getSelected');
	if (row){
		$('#dlg').dialog('open').dialog('setTitle','修改信息');
		$('#fm').form('load',row);
		url = 'updateSysSceneControl.do?';
	}
	else{
		$.messager.alert('提示','请选择一行记录！','info');
	}
}
//删除时查询场景下是否有业务字典（有：不可删；无：可删）
function deleteSysScene(){
	var row = $('#sysSecne').datagrid('getSelected');
	$.ajax({
		'async':true,
		'url': 'queryPageBusiDictControl.do?busiSceneCode='+row.busiSceneCode,
		'type': 'post',
	    'dataType':'json',
	    'success':function(data){
	    	if(data.rows.length != 0){
	    		$.messager.alert('提示','该场景存在业务字典不可删除！','info');
	    		return;
	    	}
	    	else{
	    		deleteSysSceneData();//业务场景删除
	    	}
	    }
	});	
}
//业务场景删除
function deleteSysSceneData(){
	var row = $('#sysSecne').datagrid('getSelected');
	if (row){
		$.messager.confirm('提示','确定要删除吗?',function(r){
		if (r){
			$.post('deleteSysSceneControl.do',{busiSceneCode:row.busiSceneCode},function(result){
				if (result == "success"){
					$.messager.alert('提示','删除成功','info');
					$('#sysSecne').datagrid('reload',{});  // reload the user data
				}else {
					$.messager.alert('提示','删除失败','info');
				}
			});
		}
		});
	}
	else{
		$.messager.alert('提示','请选择一行记录！','info');
	}
}
//业务场景保存时校验
function saveSysScene(){
	var busiSceneName = $('#busiSceneNameId').val().replace(/\s+/g,"");
	var busiSceneCode = $('#busiSceneCodeId').val().replace(/\s+/g,"");
	//var sceneDesc = $('#sceneDescId').val();
	var displayOrder = 1;//字段用不上后台为必填项默认设为1；现在排序按保存时间倒叙排序
	if(busiSceneName == null || busiSceneName ==""){
		$.messager.alert('提示','场景名称为必填项！','info');
		return;
	}
	if(busiSceneCode == null || busiSceneCode ==""){
		$.messager.alert('提示','场景编码为必填项！','info');
		return;
	}
	/*if(sceneDesc == null || sceneDesc ==""){
		alert("场景说明为必填项");
		return;
	}*/
	if(displayOrder == null || displayOrder ==""){
		$.messager.alert('提示','显示顺序为必填项！','info');
		return;
	}
	if(isNaN(displayOrder)){
		$.messager.alert('提示','显示顺序必须为数字！','info');
		return;
	}
	//新增时校验
	if(url == 'insertSysSceneControl.do'){
		//ajax判断数据库中是否已存在相同的场景编码
		$.ajax({
			'async':true,
			'url': 'querySysSceneDataControl.do?busiSceneCode='+busiSceneCode,
			'type': 'post',
		    'dataType':'json',
		    'success':function(data){
		    	if(data.rows.length != 0){
		    		$.messager.alert('提示','该场景编码已存在！','info');
		    		return;
		    	}
		    	else{
		    		saveSceneData();//保存数据
		    	}
		    }
		});	
	}
	//修改不校验
	else{
		saveSceneData();//保存数据
	}
}
//业务场景保存
function saveSceneData(){
	$('#fm').form('submit',{
		url: url,
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(result){
			if (result == "success"){
				 $.messager.alert('提示','保存成功','info');
				 $('#dlg').dialog('close');
				 $('#sysSecne').datagrid('reload',{});
			} else {
				$.messager.alert('提示','保存失败','info');
				$('#dlg').dialog('close'); // close the dialog
				$('#sysSecne').datagrid('reload'); // reload the user data
			}
		}
	});
}
/******************************业务字典******************************/
//根据场景查询业务字典
function querySysDic(busiSceneCode){
	$('#busiDict').datagrid('options').url = 'queryPageBusiDictControl.do';
	$('#busiDict').datagrid('load', {
		busiSceneCode: busiSceneCode
	});
}
//业务字典新增
function addBusiDict(){
	var row = $('#sysSecne').datagrid('getSelected');
	$('#dlgB').dialog('open').dialog('setTitle','增加信息');
	$('#fmB').form('clear');
	$('#fmB').form('load',{
		busiSceneCode:row.busiSceneCode
	});
	url = 'insertBusiDictControl.do';
}
//业务字典修改
function editBusiDict(){
	var row = $('#busiDict').datagrid('getSelected');
	if (row){
		$('#dlgB').dialog('open').dialog('setTitle','修改信息');
		$('#fmB').form('load',row);
		url = 'updateBusiDictControl.do?dicId='+row.dicId;
	}
	else{
		$.messager.alert('提示','请选择一行记录！','info');
	}
}
//业务字典删除
function deleteBusiDict(){
	var row = $('#busiDict').datagrid('getSelected');
	var busiSceneCode = row.busiSceneCode;
	if (row){
		$.messager.confirm('提示','确定要删除吗?',function(r){
			if (r){
				$.post('deleteBusiDictControl.do', 
					{
						busiSceneCode : row.busiSceneCode,
					  	dicId : row.dicId
					}, function(result) {
						if (result == "success") {
							$.messager.alert('提示', '删除成功', 'info');
							querySysDic(busiSceneCode);// 刷新业务字典表
						} else {
							$.messager.alert('提示', '删除失败', 'info');
						}
					});
			}
		});
	}
	else{
		$.messager.alert('提示','请选择一行记录！','info');
	}
}
//业务字典保存时校验
function saveBusiDictData(){
	var busiSceneCode = $('#busiSceneCodeIdB').val().replace(/\s+/g,"");
	var dicName = $('#dicNameId').val().replace(/\s+/g,"");
	var dicCode = $('#dicCodeId').val().replace(/\s+/g,"");
	var displayOrder = $('#displayOrderIdB').val();
	if(busiSceneCode == null || busiSceneCode ==""){
		$.messager.alert('提示','场景编码为必填项！','info');
		return;
	}
	if(dicName == null || dicName ==""){
		$.messager.alert('提示','字典名称为必填项！','info');
		return;
	}
	if(dicCode == null || dicCode ==""){
		$.messager.alert('提示','字典编码为必填项！','info');
		return;
	}
	if(displayOrder == null || displayOrder ==""){
		$.messager.alert('提示','显示顺序为必填项','info');
		return;
	}
	if(isNaN(displayOrder)){
		$.messager.alert('提示','显示顺序必须为数字！','info');
		return;
	}
	//新增时校验
	if(url == 'insertBusiDictControl.do'){
		//ajax判断数据库中是否已存在相同的字典编码
		$.ajax({
			'async':true,
			'url': 'queryAddBusiDictControl.do?busiSceneCode='+busiSceneCode+'&dicCode='+dicCode,
			'type': 'post',
		    'dataType':'json',
		    'success':function(data){
		    	if(data.rows.length != 0){
		    		$.messager.alert('提示','该场景下此字典编码已存在！','info');
		    		return;
		    	}
		    	else{
		    		saveBusiDict();//保存数据
		    	}
		    }
		});	
	}
	//修改不校验
	else{
		saveBusiDict();//保存数据
	}
}
//业务字典保存
function saveBusiDict(){
	var row1 = $('#sysSecne').datagrid('getSelected');
	$('#fmB').form('submit',{
		url: url,
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(result){
			if (result == "success"){
				 $.messager.alert('提示','保存成功','info');
				 $('#dlgB').dialog('close');
				 querySysDic(row1.busiSceneCode);//刷新业务字典表
			} else {
				$.messager.alert('提示','保存失败','info');
				$('#dlgB').dialog('close'); // close the dialog
			}
		}
	});
}