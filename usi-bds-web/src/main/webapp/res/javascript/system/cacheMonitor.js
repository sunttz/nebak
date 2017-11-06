/**
 * 缓存监控
 * @author 
 */
$(document).ready(function() {
	$('#cacheControlForm').datagrid({
		url : 'getCache.do',
		striped : true,
		fit : true,
		border : false,
		fitColumns : true,
		singleSelect : true,
		columns : [[
				{field : 'hcName',title : '缓存类别',halign : 'center',align : 'center',width : 15},
				{field : 'hcNum',title : '缓存个数/容量',halign : 'center',align : 'center',	width : 15},
				{field : 'hcCount',title : '缓存使用情况',halign : 'center',align : 'center',	width : 15,
					formatter:function(value,row,index){
	        			if(value!=null&&value<=70){
	        				return '<div class="progressbar-text" style="width:115px;border:solid 1px black;z-index:9999;text-align:center">'+value+'%</div><div class="progressbar-value" style="border:solid 1px green;width:'+value+'%;background:#008000">&nbsp;</div>';
	        			}else if(value!=null&&value>70&&value<=90){
	        				return '<div class="progressbar-text" style="width:115px;border:solid 1px black;z-index:9999;text-align:center">'+value+'%</div><div class="progressbar-value" style="border:solid 1px yellow;width:'+value+'%;background:#FFFF00">&nbsp;</div>';
	        			}else{
	        				return '<div class="progressbar-text" style="width:115px;border:solid 1px black;z-index:9999;text-align:center">'+value+'%</div><div class="progressbar-value" style="border:solid 1px red;width:'+value+'%;background:#FF0000">&nbsp;</div>';
	        			}
		        	}}
				]],
		onSelect: function(rowIndex, rowData){
			$('#cacheInportContent').val(""); 
			if(rowData.hcName != null){
				$('#cacheSearchBtn').removeAttr("disabled");
				$('#reset').removeAttr("disabled");
				$('#cacheContent').datagrid({
					url :'getCacheContent.do',
					queryParams: {
						hcName:rowData.cacheName
					},
					columns:[[
					    {field:'cacheCode',title:"缓存列表",halign : 'center',align : 'left',width:40,
					    	formatter: function(value,row,index){
					    		return rowData.hcName+"："+value;
					    	}
					    }
					]]
				});
			}
		}
	});
	
	$('#cacheContent').datagrid({
		width:'auto',
		singleSelect:true,
		striped:true,
		rownumbers:true,
		fit:true,
		fitColumns:true,
		pagination : true,
		pageSize : 20,
		columns:[[
					{field:'cacheCode',title:'缓存列表',halign : 'center',align : 'left',width:40}
				]],
		onSelect: function(rowIndex, rowData){
			loadCacheInportContent(rowData);
		}
	});
	//清除过期缓存
	$('#cleanCache').click(function() {
		//获取选中的缓存名称(业务编码)
		var selected = $("#cacheControlForm").datagrid("getSelections")[0];
		//获取选中的行号
		var rowIndex = $("#cacheControlForm").datagrid("getRowIndex",selected);
		var cacheName = null;
		if(!selected){
			$.messager.alert('提示', '请您先在左表选择缓存类别！', 'info');
			return false;
		}else{
			cacheName = selected.cacheName;
		}
		$that = $(this);
		$that.linkbutton('disable');
		
		$.ajax({
			async : false,
			cache : false,
			url : 'cleanCache.do',
			type : 'post',
			data : {"cacheName":cacheName},
			dataType : 'text',
			success : function(data) {
				$that.linkbutton('enable');
				$.messager.alert('提示','清理完成,耗时'+data+'ms','info');
				$('#cacheControlForm').datagrid({
					onLoadSuccess : function(data){
    					$('#cacheControlForm').datagrid('selectRow',rowIndex);
					}
				});
				$('#cacheControlForm').datagrid("reload");
			},
			error: function(a,b,c){
				$that.linkbutton('enable');
				alert('系统异常');
			}
		});
	});
	
	$('#cacheSearchBtn').on('click', function() {
		var row = $("#cacheControlForm").datagrid("getSelected");
		if (row) {
			$('#cacheContent').datagrid('load', {
				cacheCode : $.trim($('#cacheCode').val()),
				hcName : row.cacheName
			});
		}else {
			$.messager.alert('提示', '请您先在左表选择缓存类别！', 'info');
		}
	});
	
	$('#reset').click(function() {
		$('#cacheCode').val('');
	});
});
	
function loadCacheInportContent(rowData){
	$('#cacheInportContent').val('');
	var row1 = $('#cacheControlForm').datagrid('getSelected');
	var row2 = $('#cacheContent').datagrid('getSelected');
	$.ajax({
		async : true,
		cache : false,
		url : 'getCacheInportContent.do',
		type : 'post',
		dataType : 'text',
		data : {
			cacheName:row1.cacheName,
			cacheCode:row2.cacheCode
		},
		success : function(data) {
			$('#cacheInportContent').val(data);
		},
		error: function(a,b,c){
			alert('出现异常,该缓存可能已被删除');
		}
	});
	
}