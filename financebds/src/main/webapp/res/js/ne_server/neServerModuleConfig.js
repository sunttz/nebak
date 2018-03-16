var neServerModuleId = getQueryString('neServerModuleId'); // 关联ID

$(document).ready(function() {

    //列表初始化
    $('#listTable').datagrid({
        url:'/neServerModule/getPageAllModule.do',
        fit:true,
        fitColumns:true,
        rownumbers:true,
        pageSize : 20,
        striped:true,
        pagination:true,
        singleSelect:false,
        queryParams: {
            neServerModuleId:neServerModuleId
        },
        columns:[[
            {field:'moduleId',title:'主键ID',hidden:true,halign:'center',align:'center',width:100},
            {field:'neServerModuleId',title:'关联ID',hidden:true,halign:'center',align:'center',width:100},
            {field:'moduleName',title:'模块名',halign:'center',align:'center',width:80},
            {field:'deviceAddr',title:'设备地址',halign:'center',align:'center',width:80},
            {field:'devicePort',title:'设备端口',halign:'center',align:'center',width:50},
            {field:'bakPath',title:'备份路径',halign:'center',align:'center',width:100},
            {field:'userName',title:'用户名',halign:'center',align:'center',width:80},
            {field:'passWord',title:'密码',halign:'center',align:'center',width:80},
            {field:'operate',title:'操作',halign:'center',align:'center',width:70,
                formatter: function(value, row, index) {
                    return '<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="updateNeServerModule(\''
                        +index+'\')">修改</a>&nbsp;&nbsp;<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="delOne(\''
                        +row.moduleId+'\')">删除</a>';}}
        ]],
        toolbar: [{
            iconCls: 'icon-add',
            text: '新增',
            handler: addModule
        }]
    });

});

// 新增模块
function addModule() {
    if(neServerModuleId != null && neServerModuleId != ''){
        $("#neServerModuleId").val(neServerModuleId); // 关联ID
        $('#neServerModuleDialog').dialog({
            title: '添加模块',
            modal: true,
            buttons: '#save_btns'
        }).dialog('open');
    }else {
        $.messager.alert('提示','系统异常！','info');
    }
}

// 保存模块
function saveNeServerModule() {
    var moduleId = $("#moduleId").val().trim(); // 模块ID
    var moduleName = $("#moduleName").val().trim(); // 模块名称
    var deviceAddr = $("#deviceAddr").val().trim(); // 设备地址
    var devicePort = $("#devicePort").val().trim(); // 设备端口
    var userName = $("#userName").val().trim(); // 用户名
    var passWord = $("#passWord").val().trim(); // 密码
    var bakPath = $("#bakPath").val().trim(); // 备份路径
    var neServerModuleId = $("#neServerModuleId").val(); // 关联ID
    var flag = true;
    // 校验
    if(moduleName.length == 0) {
        $('#moduleName_box .validate_box').show();
        $('#moduleName_box .validate_msg').html('必选字段');
        flag = false;
    } else {
        $('#moduleName_box .validate_box').hide();
        $('#moduleName_box .validate_msg').html('');
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
    if(devicePort.length == 0) {
        $('#devicePort_box .validate_box').show();
        $('#devicePort_box .validate_msg').html('必选字段');
        flag = false;
    } else if (!/^([1-9][0-9]*){1,3}$/.test(devicePort)) {
        $('#devicePort_box .validate_box').show();
        $('#devicePort_box .validate_msg').html('格式不正确')
        flag = false;
    } else {
        $('#devicePort_box .validate_box').hide();
        $('#devicePort_box .validate_msg').html('');
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
    if(flag == false){
        return;
    }
    // 提交
    $.ajax({
        async : false,
        cache : false,
        type : 'POST',
        dataType : 'json',
        url : '/neServerModule/addNeserverModule.do',
        data : {
            moduleId:moduleId,moduleName:moduleName,deviceAddr:deviceAddr,devicePort:devicePort,userName:userName,passWord:passWord,bakPath:bakPath,neServerModuleId:neServerModuleId
        },
        beforeSend:ajaxLoading,//发送请求前打开进度条
        success : function(data) { // 请求成功后处理函数。
            ajaxLoadEnd();//任务执行成功，关闭进度条
            if(data == true){
                $('#neServerModuleDialog').dialog('close');
                $('#listTable').datagrid('load', {
                    neServerModuleId:neServerModuleId
                });
            }else{
                $.messager.alert('提示','保存失败！','info');
            }
        }
    });
}

// 修改模块
function updateNeServerModule(index) {
    var row = $("#listTable").datagrid('getData').rows[index];
    if(row != undefined && row != null) {
        $('#neServerModuleForm').form('load', row);
        $('#neServerModuleDialog').dialog({
            title:'修改模块信息',
            modal: true,
            buttons: '#save_btns',
            onClose: function() {
                $("#neServerModuleForm").form('reset');
                $('.validate_box').hide();
                $('.validate_msg').html('');
            }
        }).dialog('open');
    } else {
        $.messager.alert('提示','请选择您要修改的模块！','info');
    }
}

// 删除模块
function delOne(moduleId) {
    if(moduleId == null || moduleId == ""){
        $.messager.alert('提示','未指定模块!','info');
    }
    $.messager.confirm("提示", "确认删除该模块吗？", function (data) {
        if (data) {
            $.ajax({
                async : false,
                cache : false,
                type : 'POST',
                dataType : 'json',
                url : '/neServerModule/deleteNeserverModule.do',
                data : {
                    moduleId : moduleId
                },
                beforeSend:ajaxLoading,//发送请求前打开进度条
                success : function(data) { // 请求成功后处理函数。
                    ajaxLoadEnd();//任务执行成功，关闭进度条
                    if(data == false){
                        $.messager.alert('提示','删除失败!','info');
                    }else{
                        $('#listTable').datagrid('load', {
                            neServerModuleId:neServerModuleId
                        });
                    }
                }
            });
        }
    });
}

/*获取地址栏参数*/
function getQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null) return r[2]; return null;
}

//采用jquery easyui loading css效果
function ajaxLoading(){
    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body");
    $("<div class=\"datagrid-mask-msg\"></div>").html("正在处理，请稍候。。。").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});
}
function ajaxLoadEnd(){
    $(".datagrid-mask").remove();
    $(".datagrid-mask-msg").remove();
}