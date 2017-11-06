/**
 * 执行脚本配置
 * @author hu.yuchen
 */
$(document).ready(function() {
	$('#scriptsGrid').datagrid({
		url : 'getScrConfig.do',
		pagination : true,
		pageSize : 10,
		striped : true,
		//rownumbers : true,	
		fit : true,
		border : false,
		fitColumns : true,
//		singleSelect : true,
		toolbar : [{
					iconCls : 'icon-add',
					text : '发布',
					handler : publish
				}, '-', {
					iconCls : 'icon-add',
					text : '发布全部',
					handler : publishAll
				}],
		columns : [[
		        {field:'ck',checkbox:true},
				{field : 'scriptCode',title : '脚本编码',halign : 'center',align : 'center',width : 26},
				{field : 'scriptName',title : '脚本名称',halign : 'center',align : 'center',	width : 17,
					formatter : function(value, row, index) {
						if(value!=null)
							return '<span title="'+filter(value)+'">'+filter(value)+'</span>';
					}
				},
				{field : 'scriptContent',title : '脚本内容',halign : 'center',align : 'center',width : 23,
					formatter : function(value, row, index) {
						if(value!=null)
							return '<span class=" easyui-tooltip "   title="'+filter(value)+'">'+filter(value)+'</span>';
					}		
				},
				{field : 'module',title : '所属模块',halign : 'center',align : 'center',	width : 17,
					formatter : function(value, row, index) {
						for(var i=0;i<_modules.length;i++){
		        			if(value == _modules[i].dicCode){
		        				return _modules[i].dicName;
		        			}
		        		}
		        		return '未知';
					}
				},
				{field : 'scriptType',title : '脚本类型',halign : 'center',align : 'center',	width : 17,
					formatter:function(value,row,index){
						for(var i=0;i<_scriptTypes.length;i++){
		        			if(value == _scriptTypes[i].dicCode){
		        				return _scriptTypes[i].dicName;
		        			}
		        		}
		        		return '未知';
					}
				},
				{field : 'isDeploy',title : '发布状态',halign : 'center',align : 'center',	width : 9,
					formatter:function(value,row,index){
		            	  if (value==1){
		            		  return '<font color=\'red\'>已发布</font>';
			            	  } else if(value==0){
			            		  return '<font color=\'green\'>未发布</font>';
			            	  }
			              }	
				},
				{field : 'mockInparam',title : '模拟入参',halign : 'center',align : 'center',	width : 17},
				{field : 'memo',title : '备注',halign : 'center',align : 'center',	width : 18,
					formatter : function(value, row, index) {
						if(value!=null)
							return '<span class=" easyui-tooltip "   title="'+filter(value)+'">'+filter(value)+'</span>';
					}}
				]]
	});

	
//下拉框初始化
	$('#cModule').combobox({valueField : 'dicCode',textField : 'dicName',panelHeight : 'auto',editable : false,data:_modules});
	$('#cScriptType').combobox({valueField : 'dicCode',textField : 'dicName',panelHeight : 'auto',editable : false,data:_scriptTypes});

//	查询按钮
	$('#scriptsSearchBtn').on('click', function() {
		$('#scriptsGrid').datagrid('load', {
			scriptCode : $.trim($('#cScriptCode').val()),
			scriptName : $.trim($('#cScriptName').val()),
			scriptContent : $.trim($('#cScriptContent').val()),
			module : $('#cModule').combobox('getValue'),
			scriptType : $('#cScriptType').combobox('getValue')
		});
	});
	
//	重置按钮
	$('#reset').click(function() {
		$('#cScriptCode').val('');
		$('#cScriptName').val('');
		$('#cScriptContent').val('');
		$('#cModule').combobox('setValue','');
		$('#cScriptType').combobox('setValue','');
	});
});

// 发布按钮
function publish() {
	var rows = $('#scriptsGrid').datagrid('getSelections');
	var scriptCodes = [];
	if (rows!=0) {
		$.messager.confirm('提示', '是否确认发布？', function(r) {
			if (r) {
				var $mask = $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body"); 
				var $mask_msg = $("<div class=\"datagrid-mask-msg\"></div>").html("正在发布，请稍候...").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2}); 
				for(var i=0; i< rows.length; i++) scriptCodes.push(rows[i].scriptCode);
				$.ajax({
					async : true,
					cache : false,
					url : 'publishScript.do',
					type : 'post',
					data : {
						scriptCodes : scriptCodes
					},
					dataType : 'text',
					success : function(data) {
						if (data == 'success') {
							$.messager.alert('提示', '发布成功！', 'info');
							$mask_msg.remove();
							$mask.remove();
							$("#scriptsGrid").datagrid('reload');
						} else {
							$mask_msg.remove();
							$mask.remove();
							$.messager.alert('提示', '系统异常，发布失败！', 'error');
						}
					},
					error: function(a,b,c){
						$mask_msg.remove();
						$mask.remove();
						$.messager.alert('提示', '系统异常，发布失败！', 'error');
					}
				});
			}
		});
	} else {
		$.messager.alert('提示', '请选择您要发布的执行脚本！', 'info');
	}
}

//发布全部按钮
function publishAll() {
   $.messager.confirm('提示', '是否确认全部发布？', function(r) {
        if (r) {
        	var $mask = $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body"); 
        	var $mask_msg = $("<div class=\"datagrid-mask-msg\"></div>").html("正在发布，请稍候...").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2}); 
            $.ajax({
			    async : true,
			    cache : false,
			    url : 'publishAllScript.do',
				type : 'post',
				success : function(data) {
					if (data == 'success') {
						$mask_msg.remove();
						$mask.remove();
						$.messager.alert('提示', '发布成功！', 'info');
						$("#scriptsGrid").datagrid('reload');
					} else {
						$mask_msg.remove();
						$mask.remove();
						$.messager.alert('提示', '系统异常，发布失败！', 'error');
					}
				},
				error: function(a,b,c){
					$mask_msg.remove();
					$mask.remove();
					$.messager.alert('提示', '系统异常，发布失败！', 'error');
				}
			});
        }   
   });
}   

//转换html标签字符
function filter(value){
	if (typeof (value) == "string") { 
		_value = value.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g,"&quot;"); 
	} 
    return _value; 
}