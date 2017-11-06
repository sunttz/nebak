function getTodayA(){
				var date = new Date();
				var year = date.getFullYear();
			    var month = date.getMonth();
			    return new Date(year,month,'01');
}
			function getTodayB(){
				var date = new Date();
				var year = date.getFullYear();
			    var month = date.getMonth();
			    var day = date.getDate();
			    return new Date(year,month,day,'23','59','59');
			}
	
			$(document).ready(function() {
				$('#startTime').val(getTodayA().format('yyyy-MM-dd'));
				$('#endTime').val(getTodayB().format('yyyy-MM-dd'));
				$('#listTable').datagrid({
					url:'getPagedDownloadList.do',
					title:'下载列表',
					fit:true,
					fitColumns:true,
					rownumbers:true,
					striped:true,
					pagination:true,
					singleSelect:true,
					queryParams: {
						realName:$('#realName').val(),
						startTime: $('#startTime').val(),
						endTime: $('#endTime').val()
					},
					onClickCell: function (rowIndex, field, value) {										
						var row = $('#listTable').datagrid('getRows');
						  if(field =="realName"){
							  window.location.href = "downloadAttachment.do?saveName="+row[rowIndex].saveName;
						  }
				    },
					columns:[[
							{field:'realName',title:'真实名',halign:'center',align:'center',width:100,
								styler: function(value,row,index){
									return 'text-decoration:underline;color:blue;cursor:pointer;';
							    }
							},
							{field:'saveName',title:'保存名',halign:'center',align:'center',width:100},
							{field:'createTime',title:'创建时间',halign:'center',align:'center',width:100,
								formatter:function(value,row,index){
									var time = new Date(value);
			                        return time.toLocaleString();
								},							
							},
							{field:'attachmentId',title:'主键',hidden:true}
					]]
				});
				
				$('#job_log_btn').click(function(){
					$('#listTable').datagrid('load', {
						realName:$.trim($('#realName').val()),
						startTime: $('#startTime').val(),
						endTime: $('#endTime').val()
					});
				});
			});

			