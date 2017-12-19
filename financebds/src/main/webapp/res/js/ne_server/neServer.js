$(document).ready(function() {
	//地区初始化
	$("#org_id").combobox({
		url:'getAllOrg.do',
		method:'post',
		valueField:'orgId',
		textField:'orgName',
		editable:false,
		multiple:false,
		cascadeCheck:false, 
		onLoadSuccess: function () { //加载完成后,设置选中第一项
			var option = $("#org_id").combobox("getData")[0].orgId;
			$("#org_id").combobox("setValue",option);
		},
	});
	
	//列表初始化
	$('#listTable').datagrid({
		url:'getPageAllNE.do',
		title:'网元列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
		striped:true,
		pagination:true,
		singleSelect:false,
        pageSize : 20,
		queryParams: {
			orgId:$('#org_id').combobox('getValue'),
			deviceType:$('#device_type').combobox('getValue')
		},
		columns:[[
		        {field:'ck',title:'全选',checkbox:true,halign:'center',align:'center',width:100},  
				{field:'serverId',title:'主键ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgId',title:'机构ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgName',title:'所属地区',halign:'center',align:'center',width:100},
				{field:'deviceName',title:'设备名称',halign:'center',align:'center',width:100},
				{field:'deviceType',title:'网元类型',halign:'center',align:'center',width:100},
				{field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:100,
                    formatter: function(value, row, index) {
                        if(value == null || value == ""){
                            value = "-";
                        }
                        return value;
                    }},
            	{field:'bakType',title:'备份类型',halign:'center',align:'center',width:60,
                	formatter: function(value, row, index) {
						var bakType = value;
						if(value=='0'){
							bakType = "被动取";
						}else if(value=='1'){
							bakType = "主动推";
						}
						return bakType;
                }},
            	{field:'saveDay',title:'保存天数',halign:'center',align:'center',width:40},
				{field:'remarks',title:'备注',halign:'center',align:'center',width:100}
		]],
		toolbar: [{
			iconCls: 'icon-add',
			text: '备份',
			handler: function(){
				var row = $("#listTable").datagrid("getSelections");
				var ids = "";
				if(!row || row.length == 0){
					alert("请选择备份机器!");
					return;
				}else{
					for(var i=0;i<row.length;i++){
						if(i==0){
							ids=row[i].serverId;
						}else{
							ids+=","+row[i].serverId;
						}
					}
				}
				$.ajax({
					async : false,
					cache : false,
					type : 'POST',
					dataType : 'text',
					url : 'bakNow.do',
					data : {
						ids : ids
					},
					beforeSend:ajaxLoading,//发送请求前打开进度条 
					success : function(data) { // 请求成功后处理函数。
						ajaxLoadEnd();//任务执行成功，关闭进度条 
						if (data == "") {
							alert('操作成功!');
						} else {
							alert('颜色为红色是操作失败的!');
							var str= new Array();   
							str=data.split(",");   
							$('#listTable').datagrid({
								rowStyler:function(index,row){
									for (i=0;i<str.length ;i++ ){ 
										if (row.serverId==str[i]){
											return 'background-color:pink;color:red;font-weight:bold;';
										}
									}  
								}
							});
						}
					}
				});
			}
		}]
	});
	
	//点击搜索按钮
	$('#job_log_btn').click(function(){
		$('#listTable').datagrid('load', {
			orgId:$('#org_id').combobox('getValue'),
			deviceType:$('#device_type').combobox('getValue')
		});
	});
});

//采用jquery easyui loading css效果 
function ajaxLoading(){ 
    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body"); 
    $("<div class=\"datagrid-mask-msg\"></div>").html("正在处理，请稍候。。。").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2}); 
 } 
 function ajaxLoadEnd(){ 
     $(".datagrid-mask").remove(); 
     $(".datagrid-mask-msg").remove();             
} 

