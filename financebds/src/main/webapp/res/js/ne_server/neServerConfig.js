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
    $("#orgId").combobox({
        url:'getAllOrg.do',
        method:'post',
        valueField:'orgId',
        textField:'orgName',
        editable:false,
        multiple:false,
        cascadeCheck:false,
        onLoadSuccess: function () { //加载完成后,设置选中第一项
            var options = $("#orgId").combobox("getData");
            if(options[0].orgId==-1){
                options.shift();
            }
            $("#orgId").combobox('loadData',options);
        },
    });

	
	//列表初始化
	$('#listTable').datagrid({
		url:'getPageAllNE.do',
		title:'网元列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
        pageSize : 20,
		striped:true,
		pagination:true,
		singleSelect:false,
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
				{field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:100},
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
				{field:'remarks',title:'备注',halign:'center',align:'center',width:100},
				{field:'operate',title:'操作',halign:'center',align:'center',width:70,
					formatter: function(value, row, index) {
                    	return '<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="updateNeServer(\''
                        +index+'\')">修改</a>&nbsp;&nbsp;<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="delOne(\''
                        +row.serverId+'\')">删除</a>';
                }}
		]],
		toolbar: [{
			iconCls: 'icon-add',
			text: '新增',
			handler: addNeServer
		},{
            iconCls: 'icon-remove',
            text: '批量删除',
            handler: function(){
                var row = $("#listTable").datagrid("getSelections");
                var ids = "";
                if(!row || row.length == 0){
                    $.messager.alert('提示','未指定网元!','info');
                    return;
                }
                $.messager.confirm("提示", "确认批量删除吗？", function (data) {
                    if (data) {
                        for(var i=0;i<row.length;i++){
                            if(i==0){
                                ids=row[i].serverId;
                            }else{
                                ids+=","+row[i].serverId;
                            }
                        }
                        $.ajax({
                            async : false,
                            cache : false,
                            type : 'POST',
                            dataType : 'text',
                            url : 'deleteNeserver.do',
                            data : {
                                serverIds : ids
                            },
                            beforeSend:ajaxLoading,//发送请求前打开进度条
                            success : function(data) { // 请求成功后处理函数。
                                ajaxLoadEnd();//任务执行成功，关闭进度条
                                if (data == "") {
                                    $.messager.alert('提示','操作成功!','info');
                                    $('#listTable').datagrid('load', {
                                        orgId:$('#org_id').combobox('getValue'),
                                        deviceType:$('#device_type').combobox('getValue')
                                    });
                                } else {
                                    $.messager.alert('提示','颜色为红色是操作失败的!','info');
                                    var str= new Array();
                                    str=data.split(",");
                                    $('#listTable').datagrid({
                                        rowStyler:function(index,row){
                                            for (i=0;i<str.length;i++ ){
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

    $('#neServerDialog').dialog({
        title: '添加网元',
        width: 700,
        height: 'auto',
        iconCls: 'icon-save',
        closed: true,
        cache: false,
        href: '',
        modal: true,
        buttons: '#add_btns',
        onClose: function() {
            $("#neServerForm").form('reset');
            $('.validate_box').hide();
            $('.validate_msg').html('');
        }
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

// 删除网元
function delOne(serverId) {
	if(serverId == null || serverId == ""){
        $.messager.alert('提示','未指定网元!','info');
	}
    $.messager.confirm("提示", "确认删除该网元吗？", function (data) {
        if (data) {
            $.ajax({
                async : false,
                cache : false,
                type : 'POST',
                dataType : 'text',
                url : 'deleteNeserver.do',
                data : {
                    serverIds : serverId
                },
                beforeSend:ajaxLoading,//发送请求前打开进度条
                success : function(data) { // 请求成功后处理函数。
                    ajaxLoadEnd();//任务执行成功，关闭进度条
                    if(data != null && data != ""){
                        $.messager.alert('提示','删除失败!','info');
                    }else{
                        $('#listTable').datagrid('load', {
                            orgId:$('#org_id').combobox('getValue'),
                            deviceType:$('#device_type').combobox('getValue')
                        });
                    }
                }
            });
        }
    });
}

//新增网元
function addNeServer(){
    $('#neServerDialog').dialog({
        title: '添加网元'
    }).dialog('open');
}

// 保存网元
function saveNeServer() {
    var serverId = $("#serverId").val();
    var orgId = $("#orgId").combobox("getValue"); // 所属地区id
    var orgName = $("#orgId").combobox("getText"); // 所属地区name
    var deviceName = $("#deviceName").val(); // 设备名称
    var deviceType=$('input:radio[name="deviceType"]:checked').val(); // 网元类型
    var bakType = $('input:radio[name="bakType"]:checked').val(); // 备份类型
    var remarks = $("#remarks").val(); // 备注
    var deviceAddr = $("#deviceAddr").val(); // 设备地址
    var bakPath = $("#bakPath").val(); // 备份路径
    var userName = $("#userName").val(); // 用户名
    var passWord = $("#passWord").val(); // 密码
    var flag = true;
    // 校验
    if(orgId.length == 0) {
        $('#orgId_box .validate_box').show();
        $('#orgId_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#orgId_box .validate_box').hide();
        $('#orgId_box .validate_msg').html('');
    }
    if(deviceName.length == 0) {
        $('#deviceName_box .validate_box').show();
        $('#deviceName_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#deviceName_box .validate_box').hide();
        $('#deviceName_box .validate_msg').html('');
    }
    if(deviceType.length == 0) {
        $('#deviceType_box .validate_box').show();
        $('#deviceType_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#deviceType_box .validate_box').hide();
        $('#deviceType_box .validate_msg').html('');
    }
    if(bakType.length == 0) {
        $('#bakType_box .validate_box').show();
        $('#bakType_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#bakType_box .validate_box').hide();
        $('#bakType_box .validate_msg').html('');
    }
    if(remarks.length == 0) {
        $('#remarks_box .validate_box').show();
        $('#remarks_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#remarks_box .validate_box').hide();
        $('#remarks_box .validate_msg').html('');
    }
    if(deviceAddr.length == 0) {
        $('#deviceAddr_box .validate_box').show();
        $('#deviceAddr_box .validate_msg').html('必选字段');
        flag = false;
    } else if (!/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(deviceAddr)) {
        $('#deviceAddr_box .validate_box').show();
        $('#deviceAddr_box .validate_msg').html('格式不正确')
        flag = false;
    } else {
        $('#deviceAddr_box .validate_box').hide();
        $('#deviceAddr_box .validate_msg').html('');
    }
    if(bakPath.length == 0) {
        $('#bakPath_box .validate_box').show();
        $('#bakPath_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#bakPath_box .validate_box').hide();
        $('#bakPath_box .validate_msg').html('');
    }
    if(userName.length == 0) {
        $('#userName_box .validate_box').show();
        $('#userName_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#userName_box .validate_box').hide();
        $('#userName_box .validate_msg').html('');
    }
    if(passWord.length == 0) {
        $('#passWord_box .validate_box').show();
        $('#passWord_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#passWord_box .validate_box').hide();
        $('#passWord_box .validate_msg').html('');
    }
    if(flag == false){
        return;
    }
    // 提交
    $.ajax({
        async : false,
        cache : false,
        type : 'POST',
        dataType : 'json',
        url : 'saveNeserver.do',
        data : {
            serverId:serverId,orgId:orgId,orgName:orgName,deviceName:deviceName,deviceType:deviceType,bakType:bakType,remarks:remarks,deviceAddr:deviceAddr,bakPath:bakPath,userName:userName,passWord:passWord
        },
        beforeSend:ajaxLoading,//发送请求前打开进度条
        success : function(data) { // 请求成功后处理函数。
            ajaxLoadEnd();//任务执行成功，关闭进度条
            if(data == true){
                $('#neServerDialog').dialog('close');
                $('#listTable').datagrid('load', {
                    orgId:$('#org_id').combobox('getValue'),
                    deviceType:$('#device_type').combobox('getValue')
                });
            }else{
                $.messager.alert('提示','保存失败！','info');
            }
        }
    });
}

// 修改网元
function updateNeServer(index) {
    var row = $("#listTable").datagrid('getData').rows[index];
    console.info(row);
    if(row != undefined && row != null) {
        $('#neServerForm').form('load', row);
        $('#neServerDialog').dialog({
            title:'修改网元信息',
            onClose: function() {
                $("#neServerForm").form('reset');
                $('.validate_box').hide();
                $('.validate_msg').html('');
            }
        }).dialog('open');
    } else {
        $.messager.alert('提示','请选择您要修改的网元！','info');
    }
}