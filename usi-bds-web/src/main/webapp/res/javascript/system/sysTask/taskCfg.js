function validateTaskName() {
	var jobName = $('#jobName').val();
	if(jobName.length == 0) {
		$('#jobName_box .validate_box').show();
		$('#jobName_box .validate_msg').html('必选字段');
		return false;
	} else {
		$('#jobName_box .validate_box').hide();
		$('#jobName_box .validate_msg').html('');
		return true;
	}
}

function validateIpAddr() {
	var ipAddr = $('#ipAddr').val();
	if(ipAddr.length == 0) {
		$('#ipAddr_box .validate_box').show();
		$('#ipAddr_box .validate_msg').html('必选字段');
		return false;
	} else if (!/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(ipAddr)) {
		$('#ipAddr_box .validate_box').show();
		$('#ipAddr_box .validate_msg').html('格式不正确');
		return false;
    } else {
		$('#ipAddr_box .validate_box').hide();
		$('#ipAddr_box .validate_msg').html('');
		return true;
	}
}

function validateCronExpr(){
	var cronExpr = $('#cronExpr').val();
	if(cronExpr.length == 0) {
		$('#cronExpr_box .validate_box').show();
		$('#cronExpr_box .validate_msg').html('必选字段');
		return false;
	} else {
		$('#cronExpr_box .validate_box').hide();
		$('#cronExpr_box .validate_msg').html('');
		return true;
	}
}

function validateJobbean(){
	var jobBeanName = $('#jobBeanName').val();
	if(jobBeanName == ''){
		$('#jobBeanName_box .validate_box').show();
		$('#jobBeanName_box .validate_msg').html('必选字段');
		return false;
	} else {
		$('#jobBeanName_box .validate_box').hide();
		$('#jobBeanName_box .validate_msg').html('');
		return true;
	}
}
function validateJobGroup(){
	var jobGroup = $('#jobGroup').val();
	if(jobGroup == ''){
		$('#jobGroup_box .validate_box').show();
		$('#jobGroup_box .validate_msg').html('必选字段');
		return false;
	} else {
		$('#jobGroup_box .validate_box').hide();
		$('#jobGroup_box .validate_msg').html('');
		return true;
	}
}

$(document).ready(function() {
	$('#taskTable').datagrid({
		url:'getPagedJob.do',
		title:'任务列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
		striped:true,
		pagination:true,
		singleSelect:true,
		columns:[[
				{field:'jobName',title:'任务名称',halign:'center',align:'center',width:100},
				{field:'jobGroup',title:'任务分组',halign:'center',align:'center',width:100},
				{field:'jobBeanName',title:'任务实现类',halign:'center',align:'center',width:100},
				{field:'cronExpr',title:'CRON表达式',halign:'center',align:'center',width:100},
				{field:'currState',title:'当前状态',halign:'center',align:'center',width:100,
					formatter:function(value, row, index) {
						if(value == '0') {
							return '<font color="red">未启动</font>';
						} else if(value == '1') {
							return '<font color="green">已启动</font>';
						}
					}
				},
				{field:'isAutoStart',title:'是否随工程启动',halign:'center',align:'center',width:100,
					formatter:function(value, row, index) {
						if(value == '1') {
							return '是';
						} else if(value == '0') {
							return '否';
						}
					}
				},
				{field:'ipAddr',title:'服务器ip',halign:'center',align:'center',width:100},
				{field:'staffName',title:'创建人',halign:'center',align:'center',width:100},
				{field:'createTime',title:'创建时间',halign:'center',align:'center',width:100},
		        {field:'cz',title:'',halign:'center',align:'center',width:80,
		        	formatter:function(value, row, index) {
						if(row.currState == '1') {
							return '<a class="l-btn l-btn-plain" href="javascript:;" title="关闭计时器" onclick="stopJob(' + index + ');">'+
										'<span class="l-btn-left">'+
											'<span class="l-btn-text icon-stop l-btn-icon-left"></span>'+
										'</span>'+
									'</a>';
						} else if(row.currState == '0') {
							return '<a class="l-btn l-btn-plain" href="javascript:;" title="启动计时器" onclick="startJob(' + index + ');">'+
										'<span class="l-btn-left">'+
											'<span class="l-btn-text icon-start l-btn-icon-left"></span>'+
										'</span>'+
									'</a>';
						}
					}
		        }
		]]
	}).datagrid('getPager').pagination({
		buttons: [{
			iconCls:'icon-add',
			text:'添加',
			handler: addTask
		},'-',{
			iconCls:'icon-edit',
			text:'修改',
			handler: editTask
		},'-',{
			iconCls:'icon-remove',
			text:'删除',
			handler: deleteTask
		}]
	});
	
	$('#taskDialog').dialog({
		title: '添加定时任务',
		width: 700,
		height: 'auto',
		iconCls: 'icon-save',
		closed: true,
		cache: false,
		href: '',
		modal: true,
		buttons: '#add_btns',
		onClose: function() {
			$("#taskForm").form('reset');
			$('.validate_box').hide();
			$('.validate_msg').html('');
		}
	});
	
	$('#taskForm').form({
		url:'addOrDelJob.do',
		onSubmit:function(param) {
			$('#jobName').val($.trim($('#jobName').val()));
			$('#ipAddr').val($.trim($('#ipAddr').val()));
			$('#cronExpr').val($.trim($('#cronExpr').val()));
			
			return validateTaskName() & validateIpAddr() & validateCronExpr() & validateJobbean() & validateJobGroup();
		},
		success:function(data) {
			if(data == 'succ') {
				$.messager.alert('提示','操作成功！','info');
				$('#taskDialog').dialog('close');
				$("#taskTable").datagrid('reload');
			} else {
				$.messager.alert('提示','系统异常，操作失败！','error');
			}
		}
	});
});

//新增任务
function addTask(){
	$('#taskDialog').dialog({
		title:'添加定时任务'
	}).dialog('open');
}

//更新任务
function editTask(){
	var row = $('#taskTable').datagrid('getSelected');
	if(row) {
		if(row.currState == '1') {
			$.messager.alert('提示','启动状态的任务不能修改！','info');
			return;
		}
		$('#taskForm').form('load', row);
		$('#taskDialog').dialog({
			title:'修改定时任务'
		}).dialog('open');
		if(row.currState == '1') {
			$('#taskSaveBtn').linkbutton('disable');
		}
	} else {
		$.messager.alert('提示','请选择您要修改的任务！','info');
	}
}

//删除任务
function deleteTask(){
	var row = $('#taskTable').datagrid('getSelected');
	if(row) {
		if(row.currState == '1') {
			$.messager.alert('提示','启动状态的任务不能删除！','warning');
		} else {
			$.messager.confirm('提示','是否确认删除？',function(r){
				if(r){
					$.ajax({
						async : true,
						cache : false,
						url : 'deleteJob.do',
						type : 'post',
						data : {
							jobId : row.jobId
						},
						dataType : 'text',
						success: function(data) {
							if(data == 'succ') {
								$.messager.alert('提示','操作成功！','info');
								$("#taskTable").datagrid('reload');
							} else {
								$.messager.alert('提示','系统异常，操作失败！','error');
							}
						}
					});
				}
			});
		}
	} else {
		$.messager.alert('提示','请选择您要删除的任务！','info');
	}
}

//启动任务
function startJob(index) {
	var row = $('#taskTable').datagrid('getRows')[index];
	if (serverips.indexOf(row.ipAddr) == -1){
		$.messager.alert('提示','只能启动本服务器的定时任务！','info');
		return;
	}
	$.ajax({
		async : false,
		cache : false,
		url : 'startJob.do',
		type : 'post',
		data : {
			jobId : row.jobId,
			jobName : row.jobName,
			ipAddr : row.ipAddr,
			cronExpr : row.cronExpr,
			jobBeanName : row.jobBeanName,
			inParam : row.inParam
		},
		dataType : 'json',
		success : function(data) {
			if(data.flag) {
				$.messager.alert('提示','启动成功！','info');
				$('#taskTable').datagrid('updateRow',{
					index : index,
					row : {currState : '1'}
				});
			} else {
				$.messager.alert('提示', data.failCause, 'error');
			}
		}
	});
}

function saveJob(){
	$('#taskForm').submit();
}

//停止任务
function stopJob(index) {
	var row = $('#taskTable').datagrid('getRows')[index];
	if (serverips.indexOf(row.ipAddr) == -1){
		$.messager.alert('提示','只能停止本服务器的定时任务！','info');
		return;
	}
	$.ajax({
		async : false,
		cache : false,
		url : 'stopJob.do',
		type : 'post',
		data : {
			jobId : row.jobId,
			jobName : row.jobName,
			ipAddr : row.ipAddr
		},
		dataType : 'json',
		success: function(data) {
			if(data.flag) {
				$.messager.alert('提示','关闭成功！','info');
				$('#taskTable').datagrid('updateRow',{
					index : index,
					row : {currState : '0'}
				});
			} else {
				$.messager.alert('提示', data.failCause, 'error');
			}
		}
	});
}