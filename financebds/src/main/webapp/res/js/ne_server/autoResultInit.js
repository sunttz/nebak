$(document).ready(function() {
	//列表初始化
	$('#listTable').datagrid({
		url:'getAutoResult.do',
		title:'网元列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
        pageSize : 20,
		striped:true,
		pagination:true,
		singleSelect:false,
		queryParams: {
			dateTime:$('#date_time').val()
		},
		columns:[[
				{field:'serverId',title:'主键ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgId',title:'机构ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgName',title:'所属地区',halign:'center',align:'center',width:100},
				{field:'deviceName',title:'设备名称',halign:'center',align:'center',width:100},
				{field:'deviceType',title:'网元类型',halign:'center',align:'center',width:80},
				{field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:80,
                    formatter: function(value, row, index) {
                        if(value == null || value == ""){
                        	value = "-";
						}
						return value;
                    }
				},
            	{field:'bakType',title:'备份类型',halign:'center',align:'center',width:40,
                	formatter: function(value, row, index) {
						var bakType = value;
						if(value=='0'){
							bakType = "被动取";
						}else if(value=='1'){
							bakType = "主动推";
						}
						return bakType;
                }},
            	{field:'saveType',title:'保存类型',halign:'center',align:'center',width:40,
					formatter: function(value, row, index) {
						var saveType = value;
						if(value=='D'){
							saveType = "按天";
						}else if(value=='W'){
							saveType = "按周";
						}
						return saveType;
                }},
            	{field:'saveDay',title:'保存份数',halign:'center',align:'center',width:40},
				{field:'remarks',title:'备注',halign:'center',align:'center',width:100},
				{field:'bakFlag',title:'操作结果',halign:'center',align:'center',width:80,
					formatter: function(value,row,index){
		        		if(value==0){
		        			return "<font color='red'>失败</font>";
		        		}else{
		        			return "成功";
		        		}
		        	}
				},
				{field:'createDate',title:'备份时间',halign:'center',align:'center',width:100}
		]]
	});
	
	
	//点击搜索按钮
	$('#job_log_btn').click(function(){
		$('#listTable').datagrid('load', {
			dateTime:$('#date_time').val()
		});
	});
});