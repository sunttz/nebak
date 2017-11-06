$(function () {
	//自适应高度，但是要防止异常（比如别人的iframe不叫listWindow，又或者单独调试的时候，没有包含在iframe里）
	try{
		parent.document.all.listWindow.height=document.body.scrollHeight;
	}catch(e){}
	
	//取得分组
	var groupCode =$("#groupCode").val();
	//取关联主键
	var relationId=$("#relationId").val();
	
    $('#fileupload').fileupload({
        dataType: 'json',
        //额外提交的参数（值在前面已获取）
        formData:[{name:'groupCode',value:groupCode},{name:'relationId',value:relationId}],
        done: function (e, data) {
        	//$('#fileupload').fileupload('disable');
        	//$("tr:has(td)").remove();
            $.each(data.result, function (index, file) {
            	if(file.fileSize=="-1"){
            		alert("上传文件大小超过系统限制！");
            	}else{
                $("#uploaded-files").append(
                		
                		$('<tr/>')
                		.append($('<td/>').text(file.fileName))
                		.append($('<td/>').text(file.fileSize))
                		.append($('<td/>').text(file.fileType))
                		.append($('<td/>').html("<a href='getFile.do?fileId="+file.fileId+"'>下载</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='#' onclick='fileDel("+file.fileId+")'>删除</a>"))
                		);
            	}
            });
            
            //自适应高度，要求父页面iframe的那么是listWindow,要防止异常
        	try{
        		parent.document.all.listWindow.height=document.body.scrollHeight;
        	}catch(e){}
        	
        },
        //失败的毁掉函数，比如异常了
        fail:function (e, data) {
        	alert("操作失败！");
        }
        
    });
});

//删除对应的函数
function fileDel(fileId){

	if(confirm("确定删除？")){
		$.ajax({
			async : false,
			cache : false,
			type : 'POST',
			dataType : 'text',
			url : 'delFile.do',
			data : {
				fileId : fileId
			},
			success : function(data) { // 请求成功后处理函数。
				if (data == "success") {
					alert('操作成功');
					//删除行[event.srcElement获取事件源对象，$(event.srcElement)转化为jquery对象，找到最近的tr，删除]
					$(event.srcElement).closest("tr").remove();
		            //自适应高度，要求父页面iframe的那么是listWindow,要防止异常
		        	try{
		        		parent.document.all.listWindow.height=document.body.scrollHeight;
		        	}catch(e){}
		        	
				} else {
					alert('操作失败');
				}
			}
		});
	}
	
}