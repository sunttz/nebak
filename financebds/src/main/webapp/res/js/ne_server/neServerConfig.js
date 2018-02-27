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
        pageSize : 20,
		striped:true,
		pagination:true,
		singleSelect:false,
		queryParams: {
			orgId:$('#org_id').combobox('getValue'),
			deviceType:$('#device_type').combobox('getValue'),
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
				{field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:80,
                    formatter: function(value, row, index) {
                        if(value == null || value == ""){
                            value = "-";
                        }
                        return value;
                    }},
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

	// 地区
    $("#orgId").combobox({
        url:'getAllOrg2.do',
        method:'post',
        valueField:'orgId',
        textField:'orgName',
        editable:false,
        multiple:false,
        cascadeCheck:false,
        onLoadSuccess: function () {
        },
    });

    // 厂家
    $("#firms").combobox({
        url:'getAllFirms.do',
        method:'post',
        valueField:'dicName',
        textField:'dicName',
        editable:false,
        multiple:false,
        cascadeCheck:false,
        onLoadSuccess: function () {
        },
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

    // 主动推类型无需ftp配置
    $("input:radio[name=bakType]").change(function () {
        var bakType = $('input:radio[name="bakType"]:checked').val();
        // 主动推
        if(bakType == "1"){
            $("#deviceAddr").val("");
            $("#userName").val("");
            $("#passWord").val("");
            $("#bakPath").val("");
            $("#deviceAddrTr,#userNameTr,#passWordTr,#bakPathTr").hide();
            $("#bakUserdataTr,#bakSystemTr").show();
        }else{
            $("#bakUserdata").val("");
            $("#bakSystem").val("");
            $("#bakUserdataTr,#bakSystemTr").hide();
            $("#deviceAddrTr,#userNameTr,#passWordTr,#bakPathTr").show();
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
    $("#bakUserdataTr,#bakSystemTr").hide();
    $("#deviceAddrTr,#userNameTr,#passWordTr,#bakPathTr").show();
    $('#neServerDialog').dialog({
        title: '添加网元'
    }).dialog('open');
}

// 保存网元
function saveNeServer() {
    var serverId = $("#serverId").val();
    var orgId = $("#orgId").combobox("getValue"); // 所属地区id
    var orgName = $("#orgId").combobox("getText"); // 所属地区name
    var deviceName = $("#deviceName").val().trim(); // 设备名称
    var deviceType=$("#deviceType").combobox("getValue"); // 网元类型
    var firms = $("#firms").combobox("getValue"); // 厂家
    var bakType = $('input:radio[name="bakType"]:checked').val(); // 备份类型
    var saveType = $('input:radio[name="saveType"]:checked').val(); // 保存类型
    var saveDay = $("#saveDay").val().trim();// 保存天数
    var remarks = $("#remarks").val().trim(); // 备注
    var deviceAddr = $("#deviceAddr").val().trim(); // 设备地址
    var bakPath = $("#bakPath").val().trim(); // 备份路径
    var bakUserdata = $("#bakUserdata").val().trim(); // 用户数据路径
    var bakSystem = $("#bakSystem").val().trim(); // 系统数据路径
    var userName = $("#userName").val().trim(); // 用户名
    var passWord = $("#passWord").val().trim(); // 密码
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
    if(firms.length == 0) {
        $('#firms_box .validate_box').show();
        $('#firms_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#firms_box .validate_box').hide();
        $('#firms_box .validate_msg').html('');
    }
    if(bakType.length == 0) {
        $('#bakType_box .validate_box').show();
        $('#bakType_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#bakType_box .validate_box').hide();
        $('#bakType_box .validate_msg').html('');
    }
    if(saveType.length == 0) {
        $('#saveType_box .validate_box').show();
        $('#saveType_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#saveType_box .validate_box').hide();
        $('#saveType_box .validate_msg').html('');
    }
    if(saveDay.length == 0) {
        $('#saveDay_box .validate_box').show();
        $('#saveDay_box .validate_msg').html('必选字段');
        flag = false;
    }
    // 保存天数为正整数
    else if (!/^([1-9][0-9]*){1,3}$/.test(saveDay)){
        $('#saveDay_box .validate_box').show();
        $('#saveDay_box .validate_msg').html('必须为正整数');
        flag = false;
    } else {
        $('#saveDay_box .validate_box').hide();
        $('#saveDay_box .validate_msg').html('');
    }
    if(remarks.length == 0) {
        $('#remarks_box .validate_box').show();
        $('#remarks_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#remarks_box .validate_box').hide();
        $('#remarks_box .validate_msg').html('');
    }
    // 被动取类型校验ftp配置
    if(bakType == "0"){
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
        if(bakPath.length == 0) {
            $('#bakPath_box .validate_box').show();
            $('#bakPath_box .validate_msg').html('必选字段');
            flag = false;
        } else {
            $('#bakPath_box .validate_box').hide();
            $('#bakPath_box .validate_msg').html('');
        }
    }else if(bakType == "1"){
        if(bakUserdata.length == 0 && bakSystem.length == 0) {
            $('#bakUserdata_box .validate_box').show();
            $('#bakUserdata_box .validate_msg').html('至少一项不为空');
            $('#bakSystem_box .validate_box').show();
            $('#bakSystem_box .validate_msg').html('至少一项不为空');
            flag = false;
        } else {
            $('#bakUserdata_box .validate_box').hide();
            $('#bakUserdata_box .validate_msg').html('');
            $('#bakSystem_box .validate_box').hide();
            $('#bakSystem_box .validate_msg').html('');
        }
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
            serverId:serverId,orgId:orgId,orgName:orgName,deviceName:deviceName,deviceType:deviceType,firms:firms,bakType:bakType,saveType:saveType,saveDay:saveDay,remarks:remarks,deviceAddr:deviceAddr,bakPath:bakPath,userName:userName,passWord:passWord,bakUserdata:bakUserdata,bakSystem:bakSystem
        },
        beforeSend:ajaxLoading,//发送请求前打开进度条
        success : function(data) { // 请求成功后处理函数。
            ajaxLoadEnd();//任务执行成功，关闭进度条
            if(data == true){
                $('#neServerDialog').dialog('close');
                $('#listTable').datagrid('load', {
                    orgId:$('#org_id').combobox('getValue'),
                    deviceType:$('#device_type').combobox('getValue'),
                    deviceName:$('#device_name').val(),
                    bakType:$('#bak_type').combobox('getValue'),
                    saveType:$('#save_type').combobox('getValue'),
                    saveDay:$('#save_day').val()
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
    if(row != undefined && row != null) {
        // 主动推类型无需ftp配置
        var bakType = row.bakType;
        if(bakType == "1"){
            $("#deviceAddrTr,#userNameTr,#passWordTr,#bakPathTr").hide();
            $("#bakUserdataTr,#bakSystemTr").show();
        }else{
            $("#deviceAddrTr,#userNameTr,#passWordTr,#bakPathTr").show();
            $("#bakUserdataTr,#bakSystemTr").hide();
        }
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