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

    // 网元类型
    $("#device_type").combobox({
        url:'getAllDeviceType2.do',
        method:'post',
        valueField:'dicCode',
        textField:'dicName',
        editable:false,
        multiple:false,
        cascadeCheck:false,
        onLoadSuccess: function () {
            //加载完成后,设置选中第一项
            var option = $("#device_type").combobox("getData")[0].dicCode;
            $("#device_type").combobox("setValue",option);
        },
    });

    $('#progressbarDialog').dialog({
        title: '',
        width: 600,
        height: 'auto',
        iconCls: 'icon-save',
        closed: true,
        cache: false,
        href: '',
        modal: true,
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
			deviceType:"-1",
            deviceName:$('#device_name').val(),
            bakType:$('#bak_type').combobox('getValue'),
            saveType:$('#save_type').combobox('getValue'),
            saveDay:$('#save_day').val()
		},
		columns:[[
		        {field:'ck',title:'全选',checkbox:true,halign:'center',align:'center',width:100},  
				{field:'serverId',title:'主键ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgId',title:'机构ID',hidden:true,halign:'center',align:'center',width:100},
				{field:'orgName',title:'所属地区',halign:'center',align:'center',width:100},
				{field:'deviceName',title:'设备名称',halign:'center',align:'center',width:100},
				{field:'deviceType',title:'网元类型',halign:'center',align:'center',width:80},
            	{field:'firms',title:'所属厂家',halign:'center',align:'center',width:50},
                // {field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:80,
                //     formatter: function(value, row, index) {
                //         if(value == null || value == ""){
                //             value = "-";
                //         }
                //         return value;
                //     }},
                // {field:'devicePort',title:'设备端口',halign:'center',align:'center',width:50,
					// formatter: function(value, row, index) {
					// 	if(value == null || value == ""){
					// 		value = "-";
					// 	}
					// 	return value;
                // }},
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
				{field:'moduleNum',title:'模块数',halign:'center',align:'center',width:40,
					formatter: function(value, row, index) {
						if(row.bakType == "1"){
							value = "-";
						}
						return value;
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
					type : 'POST',
					dataType : 'text',
					url : 'bakNow.do',
					data : {
						ids : ids
					},
					beforeSend: ajaxLoading,//发送请求前打开进度条
					success : function(data) { // 请求成功后处理函数。
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
                        ajaxLoadEnd();
					}
				});
			}
		}]
	});
	
	//点击搜索按钮
	$('#job_log_btn').click(function(){
		$('#listTable').datagrid('load', {
			orgId:$('#org_id').combobox('getValue'),
			deviceType:$('#device_type').combobox('getValue'),
            deviceName:$('#device_name').val(),
            bakType:$('#bak_type').combobox('getValue'),
            saveType:$('#save_type').combobox('getValue'),
            saveDay:$('#save_day').val()
		});
	});
});

var timer;

function ajaxLoading(){
	$("#downloading").text("");
    $('#progressbarDialog').dialog('open');
    downloading();
}
 function ajaxLoadEnd(){
     window.clearInterval(timer);
     $('#progressbarDialog').dialog('close');
} 

function downloading() {
    window.setTimeout(function(){
        timer=window.setInterval(function(){
            $.ajax({
                type:'post',
                dataType:'json',
                url: ctx+"/netElement/nebakDownloading.do",
                success: function(data) {
                	var downloading = data.downloading;
                	console.info(downloading);
                    $("#downloading").text(downloading)
                },
                error:function(data){}
            });
        },1000);
    },200);
}