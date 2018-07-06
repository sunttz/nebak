$(document).ready(function() {
    // $('#date_time').val(getDateStr(0));
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

	function download() {
        $('#progressbarDialog').dialog('open');
        $('#progressbar').progressbar('setValue', 0);
        var count = 0; // 防止死循环
        window.setTimeout(function(){
            var timer=window.setInterval(function(){
                $.ajax({
                    type:'post',
                    dataType:'json',
                    url: ctx+"/netElement/flushProgress.do",
                    success: function(data) {
                        count ++;
                        // console.info(data.percent + "————" + count);
						if(data.percent == null || data.percent=="100.0" || count == 500){
							window.clearInterval(timer);
							$('#progressbarDialog').dialog('close');
						}else{
                            $('#progressbar').progressbar('setValue', data.percent);
                        }
                    },
                    error:function(data){}
                });
            },200);
        },200);

    }

	
	//列表初始化
	$('#listTable').datagrid({
		url:'getResult.do',
		title:'网元列表',
		fit:true,
		fitColumns:true,
		rownumbers:true,
        pageSize : 50,
		striped:true,
		pagination:true,
		singleSelect:false,
        remoteSort:false,
        sortName:'fileDate',
        sortOrder:'desc',
		queryParams: {
			orgName:$('#org_id').combobox('getText'),
			dateTime:$('#date_time').val(),
			filePath:filePath
		},
		columns:[[
		        {field:'ck',title:'全选',checkbox:true,halign:'center',align:'center',width:100},
		        {field:'filePath',title:'文件路径',hidden:true,halign:'center',align:'center',width:100},
		        {field:'isDirectory',title:'是否文件夹',hidden:true,halign:'center',align:'center',width:100},
		        {field:'orgName',title:'所属地区',halign:'center',align:'center',width:100,
		        	formatter: function(value,row,index){
		        		if(value==undefined||value==""){
		        			return tmpOrgName;
		        		}else{
		        			return value;
		        		}
		        	}
		        },
				{field:'fileName',title:'备份文件夹名称',halign:'center',align:'center',width:100,
		        	styler: function(value,row,index){
		        		if(row.isDirectory){
		        			return 'text-decoration:underline;color:blue;';
		        		}
		        	},
		        	formatter: function(value, row, index) {
		        		if(row.isDirectory){
		        			return '<a href="javascript:void(0);" class="easyui-linkbutton aBtn" onclick="editTask(\''+row.filePath+'\',\''+row.orgName+'\')">'+value+'</a>';
		        		}else{
		        			return value;
		        		}
		        	}
				},
				{field:'childFileNum',title:'备份数量',halign:'center',align:'center',width:100},
				{field:'fileDate',title:'备份时间',halign:'center',align:'center',width:100,sortable:true,
					sorter:function (a,b) {
                        a = a.split('-');
                        b = b.split('-');
                        if (a[0] == b[0]){
                            if (a[1] == b[1]){
                                return (a[2]>b[2]?1:-1);
                            } else {
                                return (a[1]>b[1]?1:-1);
                            }
                        } else {
                            return (a[0]>b[0]?1:-1);
                        }

                }},
				{field:'fileSize',title:'文件夹大小',halign:'center',align:'center',width:100}
		]],
		toolbar: [{
			iconCls: 'icon-add',
			text: '下载',
			handler: function(){
				var rows = $('#listTable').datagrid('getSelections');
				if(rows.length > 1){
					$.messager.alert('提示','请选择一条记录进行修改！','info');
				} else if(rows.length == 1){
					var row = $('#listTable').datagrid('getSelected');
					window.location.href="uploadZipFile.do?souceFileName="+row.filePath;
                    download();
				} else {
					$.messager.alert('提示','请选择您要修改的记录！','info');
				}
			}
		},'-',{
            iconCls: 'icon-remove',
            text: '删除',
            handler: function(){
                var rows = $('#listTable').datagrid('getSelections');
                if(rows.length == 0){
                    $.messager.alert('提示','请选择您要删除的记录！','info');
				}else {
                    $.messager.confirm("提示", "确认删除吗？", function (data) {
                        if (data) {
                            var delPaths = "";
                            for(var i in rows){
                                var row = rows[i];
                                delPaths += row.filePath + ",";
                            }
                            $.ajax({
                                async : false,
                                cache : false,
                                type : 'POST',
                                dataType : 'json',
                                url : 'delFilePaths.do',
                                data : {
                                    delPaths : delPaths
                                },
                                success : function(data) { // 请求成功后处理函数。
                                    if(data == true){
                                        $('#listTable').datagrid('load', {
                                            orgName:'',
                                            dateTime:$('#date_time').val(),
                                            filePath:tmpfilePath
                                        });
                                    }else{
                                        $.messager.alert('提示','删除失败！','info');
                                    }
                                }
                            });
                        }
                    });
				}
            }
		},'-',
		{
			iconCls: 'icon-edit',
			text: '返回上级目录',
			handler: function(){
				tmpfilePath=tmpfilePath.replace("\\","/");
				if(tmpfilePath==filePath){
					alert("已经是顶级目录！");
					return;
				}else{
					var str=new Array();
					str=tmpfilePath.split("/");
					var path=""
					for(var i=0;i<str.length-1;i++){
						if(i==0){
							path=str[i];
						}else{
							path+="/"+str[i];
						}
					}
					$('#listTable').datagrid('load', {
						orgName:'',
						dateTime:$('#date_time').val(),
						filePath:path
					});
					tmpfilePath=path;
				}
			}
		}]
	});


	//点击搜索按钮
	$('#job_log_btn').click(function(){
		$('#listTable').datagrid('load', {
			orgName:$('#org_id').combobox('getText'),
			dateTime:$('#date_time').val(),
			filePath:filePath
		});
	});

});


function editTask(filePath,orgName){
	tmpfilePath=filePath;
	if(orgName!=undefined&&orgName!=""){
		tmpOrgName=orgName;
	}
	
	$('#listTable').datagrid('load', {
		orgName:'',
		dateTime:'',
		filePath:filePath.replace("\\","/")
	});
}

//获取addDayCount天后的日期
function getDateStr(addDayCount){
    var dd = new Date();
    dd.setDate(dd.getDate()+addDayCount);
    var y = dd.getFullYear();
    var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
    var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate(); //获取当前几号，不足10补0
    return ""+y+m+d;
}