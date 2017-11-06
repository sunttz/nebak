function getTodayA(){
	var date = new Date();
	var year = date.getFullYear();
    var month = date.getMonth();
    var day = date.getDate();
    return new Date(year,month,day);
}
function getTodayB(){
	var date = new Date();
	var year = date.getFullYear();
    var month = date.getMonth();
    var day = date.getDate();
    return new Date(year,month,day,'23','59','59');
}

$(document).ready(function() {
	$('#startTime').val(getTodayA().format('yyyy-MM-dd hh:mm:ss'));
	$('#endTime').val(getTodayB().format('yyyy-MM-dd hh:mm:ss'));
	
	$('#logTable').datagrid({
		url:'getPagedLog.do',
		title:'日志列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
		striped:true,
		pagination:true,
		singleSelect:true,
		queryParams: {
			startTime: $('#startTime').val(),
			endTime: $('#endTime').val()
		},
		columns:[[
				{field:'jobName',title:'任务名称',halign:'center',align:'center',width:100},
				{field:'ipAddr',title:'任务所在IP',halign:'center',align:'center',width:100},
				{field:'jobStartTime',title:'任务开始时间',halign:'center',align:'center',width:100},
				{field:'jobEndTime',title:'任务结束时间',halign:'center',align:'center',width:100},
				{field:'isSuccessEnd',title:'正常结束',halign:'center',align:'center',width:100,
					formatter:function(value, row, index) {
						if(value == '0') {
							return '<font color="red">异常结束</font>';
						} else if(value == '1') {
							return '<font color="green">正常结束</font>';
						}
					}
				}
		]]
	});
	
	$('#job_log_btn').click(function(){
		$('#logTable').datagrid('load', {
			jobName: $.trim($('#jobName').val()),
			startTime: $('#startTime').val(),
			endTime: $('#endTime').val()
		});
	});
	
});
