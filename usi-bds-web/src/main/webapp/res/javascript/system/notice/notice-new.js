var myWindow;
var groupCode = 'notice';

function showDialog(msg) {
	$('#msgDialog').html(msg).show();
	setTimeout(function() {
		$('#msgDialog').hide().html('');
	}, 3000);
}

function openLoad(msg) {
	$('#loadDialog').show().find('span').html(msg);
}

function closeLoad() {
	$('#loadDialog').hide().find('span').html('');
}

function removeFile(srcElement, fileId) {
	if(window.confirm('是否确认删除？')){
		openLoad('删除中...');
		$.ajax({
			type : 'POST',
			url : ctx + '/AttachmentController/delFile.do',
			data : {
				fileId : fileId
			},
			success : function(data) {
				closeLoad();
				if (data == "success") {
					showDialog('文件删除成功');
					$(srcElement).parent('.fileItem').remove();
				} else {
					showDialog('文件删除失败');
				}
			}
		});
	}
}

$(document).ready(function() {
	
	var um = UM.getEditor('myEditor', {
    	initialFrameWidth:'70%',
    	initialFrameHeight:300,
    	//autoHeightEnabled:false,
    	toolbar:[
    	            'undo redo | emotion horizontal forecolor backcolor | bold italic underline strikethrough | superscript subscript | removeformat |',
    	            'insertorderedlist insertunorderedlist | selectall cleardoc paragraph | fontfamily fontsize' ,
    	            '| justifyleft justifycenter justifyright justifyjustify |',
    	            'link unlink fullscreen'
    	        ]
    });
	
	//留言管理按钮
	$('#replyBtn').click(function() {
		$('.xiala_panel').is(':visible')?$('.xiala_panel').hide():$('.xiala_panel').show();
	}).blur(function() {
		$('.xiala_panel').hide();
	});
	
	//擦除补偿
	$('.xiala_panel').hover(function() {
		$('#replyBtn').unbind('blur');
	},function() {
		$('#replyBtn').blur(function() {
			$('.xiala_panel').hide();
		});
	});
	
	$('.xia_item').click(function() {
		$('.xiala_head span').html($(this).html());
		$('#replyBtn').attr('value', $(this).attr('value'));
		$('.xia_item').removeClass('curr');
		$(this).addClass('curr');
	});
	
	$('#uploadBtn').hover(function() {
		$(this).addClass('fb_menu_hover').find('span').css('color', '#FFFFFF');
	}, function() {
		$(this).removeClass('fb_menu_hover').find('span').css('color', 'red');
	});
	
	 $('#fileupload').fileupload({
		dataType: 'json',
//		autoUpload: false,
	    add: function (e, data) {
	    	var size = data.files[0].size;
	    	if (size > 10 * 1024 * 1024) { // 10mb
	    		showDialog('您上传的文件文件超过大小限制');
                return;
            }
	    	if(relationId == -1) {
	    		//先存草稿，再上传
	    		$.ajax({
	    			'url': 'postNotice.do',
	    	        'type': 'post',
	    	        'data': {
	    	        	noticeId: relationId,
	    	        	noticeTitle: $('#title').val(),
	    	        	loseTime:$('#loseTime').val(),
	    				noticeContent: um.getContent(),
	    				status:0,
	    				staffId: staffId,
	    				replyFlag:$('#replyBtn').attr('value'),
	    				//不被js过滤器转义< >等特殊字符（即使过滤器不开，对功能没有影响，另外只要检查到这个参数，就不转义，而不管这个参数值是什么）
	    				inXssWhiteListQ:"true"
	    	        },
	    	        'success': function(msgMap) {
	    	        	if(msgMap.msgFlag) {
	    	        		relationId = msgMap.noticeId;
	    	        		showDialog('公告已成功保存到草稿箱');
	    	        		//上传
	    	        		data.formData =  [
	    		      		    {name:'groupCode',value:groupCode},
	    		    		    {name:'relationId',value:relationId}
	    		    		];
	    		    		data.submit();
	    	        	}
	    	        }
	    		});
	    	} else {
	    		//直接上传
	    		data.formData =  [
	      		    {name:'groupCode',value:groupCode},
	    		    {name:'relationId',value:relationId}
	    		];
	    		data.submit();
	    	}
	    },
	    submit:function(e, data) {
	    	openLoad('上传中...');
	    },
	    done: function (e, data) {
	    	closeLoad();
            $.each(data.result, function (index, file) {
            	if(file.fileSize=="-1"){
            		showDialog('您上传的文件文件超过大小限制');
            	}else{
            		$("#fileArea").append('<div class="fileItem">'+
					        					'<b class="fileIcon"></b>'+
					        					'<div class="fileName" title="' + file.fileName + '">'+
					        						file.fileName+
					        					'</div>'+
					        					'<div class="fileSize">'+
					        						file.fileSize + '<span class="fileMsg">上传完成</span>'+
					        					'</div>'+
					        					'<div onclick="removeFile(this, ' + file.fileId + ');" class="fileBtn">'+
					        						'删除'+
					        					'</div>'+
					        				'</div>');
            	}
            });
        }
	 });
	
	 /**
	  * 预览
	  */
	$('#previewBtn').click(function() {
		var noticeTitle = $('#title').val();
		$('#previewForm').find('input[name="noticeId"]').val(relationId);
		$('#previewForm').find('input[name="noticeTitle"]').val(noticeTitle);
		$('#previewForm').submit();
	});
	
	/**
	 * 发布
	 */
	$('#publishBtn').click(function() {
		openLoad('发布中...');
		var noticeTitle = $('#title').val();
//		var noticeContent = um.hasContents()?um.getAllHtml():'';
		var noticeContent = um.getContent();
		$.ajax({
			'url': 'postNotice.do',
	        'type': 'post',
	        'data': {
	        	noticeId: relationId,
	        	noticeTitle: noticeTitle,
	        	loseTime:$('#loseTime').val(),
				noticeContent:noticeContent,
				status:1,
				staffId: staffId,
				replyFlag:$('#replyBtn').attr('value'),
				//不被js过滤器转义< >等特殊字符（即使过滤器不开，对功能没有影响，另外只要检查到这个参数，就不转义，而不管这个参数值是什么）
				inXssWhiteListQ:"true"
	        },
	        'success': function(msgMap) {
	        	closeLoad();
	        	var href = $('#flag').val()=='new' ? window.location.href : ctx+'/notice/drafts.do';
	        	if(msgMap.msgFlag) {
	        		relationId = msgMap.noticeId;
	        		showDialog('公告已成功发布');
	        		setTimeout(function() {
	        			window.location.href = href;
	        		}, 1000);
	        	}
	        }
		});
	});
	
	/**
	  * 存草稿
	  */
	 $('#saveBtn').click(function() {
		 openLoad('保存中...');
		 $.ajax({
			'url': 'postNotice.do',
	        'type': 'post',
	        'data': {
	        	noticeId: relationId,
	        	noticeTitle: $('#title').val(),
	        	loseTime:$('#loseTime').val(),
				noticeContent: um.getContent(),
				status:0,
				staffId: staffId,
				replyFlag: $('#replyBtn').attr('value'),
				//不被js过滤器转义< >等特殊字符（即使过滤器不开，对功能没有影响，另外只要检查到这个参数，就不转义，而不管这个参数值是什么）
				inXssWhiteListQ:"true"
	        },
	        'success': function(msgMap) {
	        	closeLoad();
	        	if(msgMap.msgFlag) {
	        		relationId = msgMap.noticeId;
	        		showDialog('公告已成功保存到草稿箱');
	        	}
	        }
		});
	 });
	
	/**
	 * 重写
	 */
	$('#rewriteBtn').click(function() {
		openLoad('加载中...');
		setTimeout(function() {
			window.location.href = window.location.href;
		}, 100);
	});
	
	/**
	 * 返回
	 */
	$('#returnBtn').click(function() {
		window.location.href = ctx + '/notice/drafts.do';
	});
	
});